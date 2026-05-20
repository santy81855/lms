package lms.server.models.dtos;

import java.math.BigDecimal;

public class GeneratedAssignmentPlan {

    private String title;
    private String instructions;
    private BigDecimal maxPoints;

    public String getTitle() {
        return title;
    }

    public String getInstructions() {
        return instructions;
    }

    public BigDecimal getMaxPoints() {
        return maxPoints;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setInstructions(String instructions) {
        this.instructions = instructions;
    }

    public void setMaxPoints(BigDecimal maxPoints) {
        this.maxPoints = maxPoints;
    }
}