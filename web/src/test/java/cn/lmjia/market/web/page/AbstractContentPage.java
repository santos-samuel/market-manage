package cn.lmjia.market.web.page;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import java.util.function.Predicate;

/**
 * @author CJ
 */
public abstract class AbstractContentPage extends AbstractPage {

    public AbstractContentPage(WebDriver webDriver) {
        super(webDriver);
    }

    /**
     * 点击面包屑里面的第一个链接
     */
    protected void clickBreadcrumb() {
        clickBreadcrumb(e -> true);
    }

    /**
     * 点击面包屑里面的某一个链接
     *
     * @param filter 过滤器
     */
    protected void clickBreadcrumb(Predicate<WebElement> filter) {
        String href = webDriver.findElement(By.className("breadcrumb")).findElements(By.tagName("a")).stream()
                .filter(filter)
                .findFirst()
                .orElseThrow(() -> new IllegalStateException("找不到匹配的面包屑按钮"))
                .getAttribute("href");
        webDriver.get(href);
    }
}
