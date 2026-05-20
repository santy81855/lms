package lms.server.models.dtos;

public class StudentQuizOptionResponse {

    private Long id;
    private Long questionId;
    private String optionText;
    private Integer optionOrder;

    public Long getId() {
        return id;
    }

    public Long getQuestionId() {
        return questionId;
    }

    public String getOptionText() {
        return optionText;
    }

    public Integer getOptionOrder() {
        return optionOrder;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setQuestionId(Long questionId) {
        this.questionId = questionId;
    }

    public void setOptionText(String optionText) {
        this.optionText = optionText;
    }

    public void setOptionOrder(Integer optionOrder) {
        this.optionOrder = optionOrder;
    }
}