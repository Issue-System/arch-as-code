package net.trilogy.arch.adapter.Jira;

import net.trilogy.arch.adapter.FilesFacade;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.ArgumentMatchers;

import java.io.IOException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Flow;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.equalTo;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

public class JiraApiTest {

    private HttpClient mockHttpClient;
    private JiraApi jiraApi;

    @Before
    public void setUp() {
        mockHttpClient = mock(HttpClient.class);
        jiraApi = new JiraApi(mockHttpClient, "base-uri", "bulk-create-endpoint");
    }

    // TODO: WIP
    @Ignore("This is WIP.")
    @Test
    public void shouldCreateStory() throws IOException, InterruptedException {
        jiraApi.createStories(null);

        String uri = "";
        String body = "";

        var captor = ArgumentCaptor.forClass(HttpRequest.class);
        verify(mockHttpClient).send(captor.capture(), ArgumentMatchers.any());
        final HttpRequest requestMade = captor.getValue();

        assertThat(
                String.join(", ", requestMade.headers().allValues("Authorization")),
                containsString("Basic")
        );
        assertThat(
                requestMade.headers().allValues("Content-Type"),
                contains("application/json")
        );

        assertThat(
                HttpRequestParserForTests.getBody(requestMade).replaceAll(" ", ""),
                equalTo(body.replaceAll(" ", ""))
        );
    }

    // TODO: Remove when no longer needed
    @Test
    @Ignore("This is not a test. This is used to actually hit the Jira API for manual testing purposes.")
    public void NotATest_UtilToSendAnActualJiraRequest() throws IOException, InterruptedException {
        HttpResponse<String> response = new JiraApiFactory().create(new FilesFacade()).createStories(null);
        System.out.println("\n********** STATUS **********");
        System.out.println("STATUS: \n" + response.statusCode());
        System.out.println("HEADERS: \n" + response.headers());
        System.out.println("BODY: \n" + response.body());
        System.out.println("********** STATUS **********\n");
    }


    // https://stackoverflow.com/questions/59342963/how-to-test-java-net-http-java-11-requests-bodypublisher
    private static class HttpRequestParserForTests<T> implements Flow.Subscriber<T> {
        private final CountDownLatch latch = new CountDownLatch(1);
        private List<T> bodyItems = new ArrayList<>();

        public static String getBody(HttpRequest fromHttpRequest) {
            final Optional<HttpRequest.BodyPublisher> maybeBodyPublisher = fromHttpRequest.bodyPublisher();
            if (maybeBodyPublisher.isEmpty()) return "";
            final HttpRequest.BodyPublisher bodyPublisherOfRequestMade = maybeBodyPublisher.get();
            HttpRequestParserForTests<ByteBuffer> httpRequestParserForTests = new HttpRequestParserForTests<>();
            bodyPublisherOfRequestMade.subscribe(httpRequestParserForTests);
            final List<ByteBuffer> bodyItems = httpRequestParserForTests.getBodyItems();
            final byte[] array = bodyItems.get(0).array();
            return new String(array);
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
