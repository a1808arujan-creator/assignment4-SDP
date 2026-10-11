package kz.aitu.registration.workflow;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class WorkflowRepository {

    private static final String FILE_PATH =
            "data/workflows.json";

    private final ObjectMapper objectMapper;

    public WorkflowRepository() {
        this.objectMapper = new ObjectMapper();
    }

    public void save(WorkflowRecord record) {

        List<WorkflowRecord> records = findAll();

        records.removeIf(existing ->
                existing.getWorkflowId()
                        .equals(record.getWorkflowId()));

        records.add(record);

        writeAll(records);
    }

    public WorkflowRecord findByWorkflowId(String workflowId) {

        return findAll()
                .stream()
                .filter(record ->
                        record.getWorkflowId()
                                .equals(workflowId))
                .findFirst()
                .orElse(null);
    }

    public List<WorkflowRecord> findAll() {

        File file = new File(FILE_PATH);

        if (!file.exists()) {
            return new ArrayList<>();
        }

        try {
            return objectMapper.readValue(
                    file,
                    new TypeReference<List<WorkflowRecord>>() {}
            );

        } catch (IOException e) {
            throw new RuntimeException(
                    "Could not read workflow data",
                    e
            );
        }
    }

    private void writeAll(List<WorkflowRecord> records) {

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
                    "Could not save workflow data",
                    e
            );
        }
    }
}