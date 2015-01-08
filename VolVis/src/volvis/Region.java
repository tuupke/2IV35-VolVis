package volvis;

public class Region {
    
    private int top;
    private int bottom;
    
    public Region(int t, int b){
        top = t;
        bottom = b;
    }
    
    public int top(){
        return top;
    }
    
    public int bottom(){
        return bottom;
    }
    
    public void setValues(int t, int b){
        top = t;
        bottom = b;
    }
}
