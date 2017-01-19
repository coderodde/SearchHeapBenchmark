import tests.UnindexedHeapCorrectnessTest;

public class Main {

    public static void main(String[] args) {
        System.out.println("Heaps behave equally: " + 
                UnindexedHeapCorrectnessTest.test());
    }
}
