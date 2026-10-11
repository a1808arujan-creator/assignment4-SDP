package kz.aitu.registration.eligibility;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class EligibilityRepository {

    private static final String FILE_PATH = "data/eligibility.json";

    private final ObjectMapper objectMapper;

    public EligibilityRepository() {
        this.objectMapper = new ObjectMapper();
    }

    public void save(EligibilityRecord record) {

        List<EligibilityRecord> records = findAll();

        records.removeIf(existing ->
                existing.getWorkflowId().equals(record.getWorkflowId()));

        records.add(record);

        writeAll(records);
    }

    public EligibilityRecord findByWorkflowId(String workflowId) {

        return findAll()
                .stream()
                .filter(record ->
                        record.getWorkflowId().equals(workflowId))
                .findFirst()
                .orElse(null);
    }

    public List<EligibilityRecord> findAll() {

        File file = new File(FILE_PATH);

        if (!file.exists()) {
            return new ArrayList<>();
        }

        try {
            return objectMapper.readValue(
                    file,
                    new TypeReference<List<EligibilityRecord>>() {}
            );

        } catch (IOException e) {
            throw new RuntimeException(
                    "Could not read eligibility data",
                    e
            );
        }
    }

    private void writeAll(List<EligibilityRecord> records) {

        try {

            File file = new File(FILE_PATH);

            File parent = file.getParentFile();

            if (parent != null && !parent.exists()) {
                parent.mkdirs();
            }

            objectMapper
                    .writerWithDefaultPrettyPrinter()
                    .writeValue(file, records);

        } catch (IOException e) {
            throw new RuntimeException(
                    "Could not save eligibility data",
                    e
            );
        }
    }
}