package dvoraka.archbuilder.prototype.net;

import dvoraka.archbuilder.prototype.data.net.PBalanceNetAdapter;
import dvoraka.archbuilder.prototype.data.net.PBalanceNetComponent;

public class NetComponentApp {

    public static void main(String[] args) {
        System.out.println("Net component app");

        PBalanceNetComponent netComponent = new PBalanceNetAdapter();
    }
}
