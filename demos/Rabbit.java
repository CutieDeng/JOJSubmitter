package dsaa.lab01; 

// 简单的兔兔问题模拟器
public class Rabbit implements Cloneable{
    private static double deathRate; 

    private int number; 

    public static void main(String[] args) {
        System.out.println("这道题由读者自行练习。");
    }

    @Override 
    public Rabbit clone() {
        try {
            Object o = super.clone(); 
            return (Rabbit)o; 
        } catch (Exception e) {
            throw new RuntimeException(e); 
        }
    }
}