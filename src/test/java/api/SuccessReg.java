package api;

public class SuccessReg {
    public SuccessReg(Integer id, String token) {
        this.id = id;
        this.token = token;
    }

    private Integer id;
    private String token;

    public Integer getId() {
        return id;
    }

    public String getToken() {
        return token;
    }
}


