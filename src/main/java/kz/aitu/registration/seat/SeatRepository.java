package kz.aitu.registration.seat;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class SeatRepository {

    private static final String FILE_PATH = "data/seats.json";

    private final ObjectMapper objectMapper;

    public SeatRepository() {
        this.objectMapper = new ObjectMapper();
    }

    public void save(SeatReservation reservation) {

        List<SeatReservation> reservations = findAll();

        reservations.removeIf(existing ->
                existing.getWorkflowId()
                        .equals(reservation.getWorkflowId()));

        reservations.add(reservation);

        writeAll(reservations);
    }

    public SeatReservation findByWorkflowId(String workflowId) {

        return findAll()
                .stream()
                .filter(reservation ->
                        reservation.getWorkflowId()
                                .equals(workflowId))
                .findFirst()
                .orElse(null);
    }

    public List<SeatReservation> findAll() {

        File file = new File(FILE_PATH);

        if (!file.exists()) {
            return new ArrayList<>();
        }

        try {
            return objectMapper.readValue(
                    file,
                    new TypeReference<List<SeatReservation>>() {}
            );

        } catch (IOException e) {
            throw new RuntimeException(
                    "Could not read seat data",
                    e
            );
        }
    }

    private void writeAll(List<SeatReservation> reservations) {

        try {

            File file = new File(FILE_PATH);

            File parent = file.getParentFile();

            if (parent != null && !parent.exists()) {
                parent.mkdirs();
            }

            objectMapper
                    .writerWithDefaultPrettyPrinter()
                    .writeValue(file, reservations);

        } catch (IOException e) {
            throw new RuntimeException(
                    "Could not save seat data",
                    e
            );
        }
    }
}