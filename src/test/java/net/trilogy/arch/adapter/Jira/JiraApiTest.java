package net.trilogy.arch.adapter.Jira;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import net.trilogy.arch.domain.architectureUpdate.FunctionalRequirement;
import net.trilogy.arch.domain.architectureUpdate.Jira;
import net.trilogy.arch.domain.architectureUpdate.Tdd;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ErrorCollector;
import org.mockito.ArgumentCaptor;
import org.mockito.ArgumentMatchers;

import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Flow;

import static net.trilogy.arch.TestHelper.JSON_JIRA_CREATE_STORIES_REQUEST_EXPECTED_BODY;
import static net.trilogy.arch.TestHelper.JSON_JIRA_GET_EPIC;
import static net.trilogy.arch.TestHelper.JSON_STRUCTURIZR_BIG_BANK;
import static net.trilogy.arch.TestHelper.loadResource;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.equalTo;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class JiraApiTest {
    @Rule
    public final ErrorCollector collector = new ErrorCollector();

    private HttpClient mockHttpClient;
    private JiraApi jiraApi;

    @Before
    public void setUp() {
        mockHttpClient = mock(HttpClient.class);
        jiraApi = new JiraApi(mockHttpClient, "http://base-uri/", "/get-story-endpoint/", "/bulk-create-endpoint");
    }

    @Test
    public void shouldMakeRequestToGetJiraStory() throws Exception {
        // GIVEN:
        @SuppressWarnings("rawtypes") HttpResponse mockedResponse = mock(HttpResponse.class);
        when(mockedResponse.body()).thenReturn(loadResource(getClass(), JSON_JIRA_GET_EPIC));
        when(mockHttpClient.send(any(), any())).thenReturn(mockedResponse);
        final Jira jiraToQuery = new Jira("JIRA-TICKET-123", "http://link");

        // WHEN:
        jiraApi.getStory(jiraToQuery, "username", "password".toCharArray());

        // THEN:
        var captor = ArgumentCaptor.forClass(HttpRequest.class);
        verify(mockHttpClient).send(captor.capture(), any());

        final HttpRequest requestMade = captor.getValue();

        collector.checkThat(
                requestMade.method(),
                equalTo("GET")
        );

        collector.checkThat(
                String.join(", ", requestMade.headers().allValues("Authorization")),
                containsString("Basic dXNlcm5hbWU6cGFzc3dvcmQ=")
        );

        collector.checkThat(
                String.join(", ", requestMade.headers().allValues("Content-Type")),
                containsString("application/json")
        );

        collector.checkThat(
                requestMade.uri().toString(),
                equalTo("http://base-uri/get-story-endpoint/" + jiraToQuery.getTicket())
        );
    }

    @Test
    public void shouldParseResponseWhenGetJiraStory() throws Exception {
        // GIVEN:
        @SuppressWarnings("rawtypes") HttpResponse mockedResponse = mock(HttpResponse.class);
        when(mockedResponse.body()).thenReturn(loadResource(getClass(), JSON_JIRA_GET_EPIC));
        when(mockHttpClient.send(any(), any())).thenReturn(mockedResponse);

        final JiraQueryResult result = jiraApi.getStory(new Jira("A", "B"), "u", "p".toCharArray());

        collector.checkThat(result.getProjectId(), equalTo("10809"));
        collector.checkThat(result.getProjectKey(), equalTo("MOFE-12"));
    }

    @Test(expected = JiraApi.GetStoryException.class)
    public void shouldThrowPresentableExceptionIfGetStoryFails() throws Exception {
        @SuppressWarnings("rawtypes") HttpResponse mockedResponse = mock(HttpResponse.class);
        when(mockedResponse.body()).thenReturn(loadResource(getClass(), JSON_STRUCTURIZR_BIG_BANK)); // <-- this is the wrong response
        when(mockHttpClient.send(any(), any())).thenReturn(mockedResponse);

        jiraApi.getStory(new Jira("A", "B"), "u", "p".toCharArray());
    }

    @Ignore("TODO")
    @Test
    public void shouldFailIfJiraDoesNotRespondWith2xxForCreateStory() {
        collector.addError(new RuntimeException("WIP"));
    }

    @Ignore("TODO")
    @Test
    public void shouldFailIfJiraDoesNotRespondWith2xxForGetStory() {
        collector.addError(new RuntimeException("WIP"));
    }

    @Ignore("TODO")
    @Test
    public void shouldParseJiraResponseOfCreateStories() {
        collector.addError(new RuntimeException("WIP"));
    }

    @Test
    public void shouldMakeCreateStoryRequestWithCorrectBody() throws Exception {
        // GIVEN:
        List<JiraStory> sampleJiraStories = createSampleJiraStories();

        // WHEN:
        jiraApi.createStories(sampleJiraStories, "EPIC KEY", "PROJECT ID", "PROJECT KEY", "username", "password".toCharArray());

        // THEN:
        var captor = ArgumentCaptor.forClass(HttpRequest.class);
        verify(mockHttpClient).send(captor.capture(), ArgumentMatchers.any());

        String expectedBody = loadResource(getClass(), JSON_JIRA_CREATE_STORIES_REQUEST_EXPECTED_BODY);
        String actualBody = HttpRequestParserForTests.getBody(captor.getValue());

        var expectedBodyJson = new ObjectMapper().readValue(expectedBody, JsonNode.class);
        var actualBodyJson = new ObjectMapper().readValue(actualBody, JsonNode.class);

        collector.checkThat(actualBodyJson, equalTo(expectedBodyJson));
    }

    @Test
    public void shouldMakeCreateStoryRequestWithCorrectHeaders() throws Exception {
        jiraApi.createStories(createSampleJiraStories(), "EPIC KEY", "PROJECT ID", "PROJECT KEY", "username", "password".toCharArray());

        var captor = ArgumentCaptor.forClass(HttpRequest.class);
        verify(mockHttpClient).send(captor.capture(), ArgumentMatchers.any());
        final HttpRequest requestMade = captor.getValue();

        collector.checkThat(requestMade.method(), equalTo("POST"));

        collector.checkThat(
                String.join(", ", requestMade.headers().allValues("Authorization")),
                containsString("Basic dXNlcm5hbWU6cGFzc3dvcmQ=")
        );

        collector.checkThat(
                requestMade.headers().allValues("Content-Type"),
                contains("application/json")
        );
    }

    private List<JiraStory> createSampleJiraStories() {
        var jiraTdd1 = new JiraStory.JiraTdd(
                new Tdd.Id("TDD ID 1"),
                new Tdd("TDD TEXT 1"),
                new Tdd.ComponentReference("COMPONENT ID 1")
        );

        var jiraTdd2 = new JiraStory.JiraTdd(
                new Tdd.Id("TDD ID 2"),
                new Tdd("TDD TEXT 2"),
                new Tdd.ComponentReference("COMPONENT ID 2")
        );

        var jiraFunctionalRequirement1 = new JiraStory.JiraFunctionalRequirement(
                new FunctionalRequirement.Id("FUNCTIONAL REQUIREMENT ID 1"),
                new FunctionalRequirement(
                        "FUNCTIONAL REQUIREMENT TEXT 1",
                        "FUNCTIONAL REQUIREMENT SOURCE 1",
                        List.of(new Tdd.Id("TDD REFERENCE 1"))
                )
        );
        var jiraFunctionalRequirement2 = new JiraStory.JiraFunctionalRequirement(
                new FunctionalRequirement.Id("FUNCTIONAL REQUIREMENT ID 2"),
                new FunctionalRequirement(
                        "FUNCTIONAL REQUIREMENT TEXT 2",
                        "FUNCTIONAL REQUIREMENT SOURCE 2",
                        List.of(new Tdd.Id("TDD REFERENCE 2"))
                )
        );

        return List.of(
                new JiraStory("STORY TITLE 1", List.of(jiraTdd1), List.of()),
                new JiraStory("STORY TITLE 2", List.of(jiraTdd1, jiraTdd2), List.of(jiraFunctionalRequirement1, jiraFunctionalRequirement2))
        );
    }

    /**
     * Used for parsing request objects, to ensure in tests we're creating the right ones.
     * See: https://stackoverflow.com/questions/59342963/how-to-test-java-net-http-java-11-requests-bodypublisher
     */
    private static class HttpRequestParserForTests<T> implements Flow.Subscriber<T> {
        private final CountDownLatch latch = new CountDownLatch(1);
        private final List<T> bodyItems = new ArrayList<>();

        public static String getBody(HttpRequest fromHttpRequest) {
            try {
                final Optional<HttpRequest.BodyPublisher> maybeBodyPublisher = fromHttpRequest.bodyPublisher();
                if (maybeBodyPublisher.isEmpty()) return "";
                final HttpRequest.BodyPublisher bodyPublisherOfRequestMade = maybeBodyPublisher.get();
                HttpRequestParserForTests<ByteBuffer> httpRequestParserForTests = new HttpRequestParserForTests<>();
                bodyPublisherOfRequestMade.subscribe(httpRequestParserForTests);
                final List<ByteBuffer> bodyItems = httpRequestParserForTests.getBodyItems();
                final byte[] array = bodyItems.get(0).array();
                return new String(array);
            } catch (Throwable e) {
                throw new RuntimeException("Unable to parse body", e);
            }
        }

        private List<T> getBodyItems() {
            try {
                this.latch.await();
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            return bodyItems;
        }

        @Override
        public void onSubscribe(Flow.Subscription subscription) {
            //Retrieve all parts
            subscription.request(Long.MAX_VALUE);
        }

        @Override
        public void onNext(T item) {
            this.bodyItems.add(item);
        }

        @Override
        public void onError(Throwable throwable) {
            this.latch.countDown();
        }

        @Override
        public void onComplete() {
            this.latch.countDown();
        }
    }

}
