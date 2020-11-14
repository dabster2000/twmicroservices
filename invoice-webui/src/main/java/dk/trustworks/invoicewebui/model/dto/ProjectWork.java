package dk.trustworks.invoicewebui.model.dto;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateSerializer;
import dk.trustworks.invoicewebui.model.Project;
import dk.trustworks.invoicewebui.utils.DateUtils;

import java.time.LocalDate;
import java.util.Arrays;

public class ProjectWork {

    private final Project project;
    @JsonSerialize(using = LocalDateSerializer.class)
    @JsonDeserialize(using = LocalDateDeserializer.class)
    private final LocalDate from;
    @JsonSerialize(using = LocalDateSerializer.class)
    @JsonDeserialize(using = LocalDateDeserializer.class)
    private final LocalDate to;
    private final double[] work;

    /**
     *
     * @param from inclusive
     * @param to exclusive
     */
    public ProjectWork(Project project, LocalDate from, LocalDate to) {
        this.project = project;
        this.from = from;
        this.to = to;
        this.work = new double[DateUtils.countDaysBetween(from, to)];
    }

    public LocalDate getFrom() {
        return from;
    }

    public LocalDate getTo() {
        return to;
    }

    public double[] getWork() {
        return work;
    }

    public Project getProject() {
        return project;
    }

    @Override
    public String toString() {
        return "ProjectWork{" +
                "project=" + project +
                ", from=" + from +
                ", to=" + to +
                ", work=" + Arrays.toString(work) +
                '}';
    }
}
