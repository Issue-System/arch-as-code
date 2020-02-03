package net.nahknarmi.arch.generator;

import com.structurizr.model.Element;
import com.structurizr.model.IdGenerator;
import com.structurizr.model.Person;
import com.structurizr.model.Relationship;
import lombok.AllArgsConstructor;
import lombok.NonNull;
import net.nahknarmi.arch.domain.c4.C4Model;
import net.nahknarmi.arch.domain.c4.C4Path;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

@AllArgsConstructor
public class PathIdGenerator implements IdGenerator {
    @NonNull
    C4Model dataStructureModel;

    @Override
    public String generateId(Element element) {
        C4Path path = buildPath(element);
        String pathString = path.getPath();
        return pathString;
    }

    @Override
    public String generateId(Relationship relationship) {
        String relationshipString = relationship.toString();
        MessageDigest md;
        try {
            md = MessageDigest.getInstance("SHA-256");
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            return null;
        }
        byte[] hashInBytes = md.digest(relationshipString.getBytes(StandardCharsets.UTF_8));

        // bytes to hex
        StringBuilder sb = new StringBuilder();
        for (byte b : hashInBytes) {
            sb.append(String.format("%02x", b));
        }
        String id = sb.toString();

        return id;
    }

    @Override
    public void found(String id) {
    }

    private C4Path buildPath(Element element) {
        if (element.getParent() == null) {
            String prefix = "c4://";

            if (element instanceof Person) {
                prefix = "@";
            }

            String path = prefix + element.getName().replaceAll("/", "-");
            return new C4Path(path);
        }

        String c4Path = buildPath(element.getParent()).getPath();
        String fullPath = c4Path + "/" + element.getName().replaceAll("/", "-");
        return new C4Path(fullPath);
    }
}
