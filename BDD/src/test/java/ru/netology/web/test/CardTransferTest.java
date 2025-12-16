package ru.netology.web.test;

import com.codeborne.selenide.Selenide;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.netology.web.data.DataHelper;
import ru.netology.web.page.DashboardPage;
import ru.netology.web.page.LoginPage;
import static org.junit.jupiter.api.Assertions.assertEquals;



public class CardTransferTest {
    DashboardPage dashboardPage;
    DataHelper.CardInfo firstCardInfo;
    DataHelper.CardInfo secondCardInfo;
    int firstCardBalance;
    int secondCardBalance;

    @BeforeEach
    void setup() {
        var loginPage = Selenide.open("http://localhost:9999", LoginPage.class);
        var authInfo = DataHelper.getAuthInfo();
        var verificationPage = loginPage.validLogin(authInfo);
        var verificationCode = DataHelper.getVerificationCode();
        dashboardPage = verificationPage.validVerify(verificationCode);
        secondCardInfo = DataHelper.getSecondCardInfo();
        firstCardInfo = DataHelper.getFirstCardInfo();
        firstCardBalance = dashboardPage.getCardBalance(firstCardInfo);
        secondCardBalance = dashboardPage.getCardBalance(secondCardInfo);

    }
// валидные тесты
    @Test
    void shouldTransferMoneyBetweenOwnCards() {
        var amount = 5000;
        var transferPage = dashboardPage.selectCardToTransfer(firstCardInfo);
        dashboardPage = transferPage.makeValidTransferMoney(String.valueOf(amount), secondCardInfo);
        assertEquals(firstCardBalance + amount, dashboardPage.getCardBalance(firstCardInfo));
        assertEquals(secondCardBalance - amount, dashboardPage.getCardBalance(secondCardInfo));

    }
    @Test
    void shouldTransferMoneyFromSecondToFirstCard() {
        int amount = 3000; // Сумма для перевода
        var transferPage = dashboardPage.selectCardToTransfer(secondCardInfo);
        dashboardPage = transferPage.makeValidTransferMoney(String.valueOf(amount), firstCardInfo);
        assertEquals(firstCardBalance - amount, dashboardPage.getCardBalance(firstCardInfo));
        assertEquals(secondCardBalance + amount, dashboardPage.getCardBalance(secondCardInfo));
    }
// не валидный тест
    @Test
    void shouldTransferNotValidMoney() {
        int amount = secondCardBalance + 20000;
        var transferPage = dashboardPage.selectCardToTransfer(firstCardInfo);
        transferPage.makeTransfer(String.valueOf(amount), secondCardInfo);
        transferPage.findErrorMessage("Ошибка");
        dashboardPage.reloadDashboardPage();
        dashboardPage.checkCardBalance(firstCardInfo, firstCardBalance);
        dashboardPage.checkCardBalance(secondCardInfo, secondCardBalance);
    }
}