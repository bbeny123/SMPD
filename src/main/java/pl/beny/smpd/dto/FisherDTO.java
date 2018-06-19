package pl.beny.smpd.dto;

public class FisherDTO {

    private String fisher = "n too big to compute Fisher";
    private long fTime;
    private String sfs;
    private long sTime;
    private String times;
    private boolean fExists;

    public void addFisher(String fisher, long fTime) {
        this.fisher = fisher;
        this.fTime = fTime;
        this.fExists = true;
    }

    public void addSFS(String sfs, long sTime) {
        this.sfs = sfs;
        this.sTime = sTime;
        setTimes();
    }

    private void setTimes() {
        if (!fExists) return;
        if (sTime == fTime) times = "Execution time of both methods was equal.";
        else if (sTime < fTime) times = String.format("SFS was faster by %d times.", fTime / sTime);
        else times = String.format("Fisher was faster by %d times.", sTime / fTime);
    }

    public String getFisher() {
        return fisher;
    }

    public void setFisher(String fisher) {
        this.fisher = fisher;
    }

    public long getfTime() {
        return fTime;
    }

    public void setfTime(long fTime) {
        this.fTime = fTime;
    }

    public String getSfs() {
        return sfs;
    }

    public void setSfs(String sfs) {
        this.sfs = sfs;
    }

    public long getsTime() {
        return sTime;
    }

    public void setsTime(long sTime) {
        this.sTime = sTime;
    }

    public String getTimes() {
        return times;
    }

    public void setTimes(String times) {
        this.times = times;
    }

    public boolean isfExists() {
        return fExists;
    }

    public void setfExists(boolean fExists) {
        this.fExists = fExists;
    }
}
