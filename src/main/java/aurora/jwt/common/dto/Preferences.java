package aurora.jwt.common.dto;

public class Preferences {

    private String locale;
    private String timezone;
    private String fileEncoding;

    public Preferences() {}
    public Preferences(String locale, String timezone, String fileEncoding) {
        this.locale = locale;
        this.timezone = timezone;
        this.fileEncoding = fileEncoding;
    }

    public String getLocale() {
        return locale;
    }

    public void setLocale(String locale) {
        this.locale = locale;
    }

    public String getTimezone() {
        return timezone;
    }

    public void setTimezone(String timezone) {
        this.timezone = timezone;
    }

    public String getFileEncoding() {
        return fileEncoding;
    }

    public void setFileEncoding(String fileEncoding) {
        this.fileEncoding = fileEncoding;
    }
}
