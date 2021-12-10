package application.ui;

import java.io.BufferedReader;
import java.io.InputStreamReader;

public class Console {
    private AdminConsole adminConsole;
    private UserConsole userConsole;
    private BufferedReader br;

    public Console(AdminConsole adminConsole, UserConsole userConsole){
        this.adminConsole = adminConsole;
        this.userConsole = userConsole;
        try{
            br = new BufferedReader(new InputStreamReader(System.in));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void printMenu(){
        System.out.println("0. Exit");
        System.out.println("1. Login as Admin");
        System.out.println("2. Login as User");
    }
    public void run(){

        int cmd = -1;
        while (cmd != 0){
            try{
                printMenu();
                System.out.print(">>>");

                String line = br.readLine();
                cmd = Integer.parseInt(line);

                switch (cmd){
                    case 0 -> {}
                    case 1 -> adminConsole.run();
                    case 2 -> userConsole.run();
                    default -> System.out.println("Invalid command!");
                }

            } catch (NumberFormatException e){
                System.out.println("Invalid number!");
            } catch (Exception e) {
                System.out.println(e.getMessage());
            }
        }
    }
}
