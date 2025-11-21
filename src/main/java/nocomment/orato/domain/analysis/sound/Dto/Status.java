package nocomment.orato.domain.analysis.sound.Dto;


import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Status {

    private int status;
    private String message;



    public Status(int status) {
        this.status = status;
    }

    public Status(int status, String message) {
        this.status = status;
        this.message = message;
    }
}