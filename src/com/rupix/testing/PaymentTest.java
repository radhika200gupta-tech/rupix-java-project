
package com.rupix.testing;

import com.rupix.service.WalletService;
import com.rupix.service.PaymentService;
import com.rupix.ui.PaymentMenu;

import java.util.Scanner;

public class PaymentTest {

    public static void main(String[] args) {

        WalletService walletService =
                new WalletService();

        PaymentService paymentService =
                new PaymentService(walletService);

        Scanner scanner = new Scanner(System.in);

        PaymentMenu paymentMenu =
                new PaymentMenu(
                        walletService,
                        paymentService
                );

        paymentMenu.show(scanner);

        System.out.println(
                "RUPIX Payment Module Closed."
        );
    }
}
