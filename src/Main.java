import java.io.FileNotFoundException;
import java.io.IOException;

public class Main {
    public static void main(String[] args) throws IOException {
        Model model = new Model();
        MainView mainView = new MainView(model);
    }
}
