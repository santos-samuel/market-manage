package cn.lmjia.market.manage.controller;

import cn.lmjia.market.core.entity.MainProduct;
import cn.lmjia.market.core.repository.MainProductRepository;
import cn.lmjia.market.core.row.FieldDefinition;
import cn.lmjia.market.core.row.RowCustom;
import cn.lmjia.market.core.row.RowDefinition;
import cn.lmjia.market.core.row.field.FieldBuilder;
import cn.lmjia.market.core.row.field.Fields;
import cn.lmjia.market.core.row.supplier.JQueryDataTableDramatizer;
import me.jiangcai.logistics.haier.HaierSupplier;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.Order;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;

/**
 * 货品管理
 * 增加推送至日日顺的功能
 * 如果资料未全则不让推送
 * <p>
 * 允许编辑但是跟推送相关的资源 一旦存在值就不可编辑
 *
 * @author CJ
 */
@Controller
@PreAuthorize("hasRole('ROOT')")
public class ManageProductController {

    @Autowired
    private MainProductRepository mainProductRepository;
    @Autowired
    private HaierSupplier haierSupplier;

    // 禁用和恢复
    @DeleteMapping("/products")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Transactional
    public void disable(String code) {
        mainProductRepository.getOne(code).setEnable(false);
    }

    @PutMapping("/products")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Transactional
    public void enable(String code) {
        mainProductRepository.getOne(code).setEnable(true);
    }

    // 推送
    @PutMapping("/productsHaier")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void pushHaier(String code) {
        MainProduct product = mainProductRepository.getOne(code);
        // 信息校验下先
        if (StringUtils.isEmpty(product.getBrand()))
            throw new IllegalArgumentException("");
        if (StringUtils.isEmpty(product.getUnit()))
            throw new IllegalArgumentException("");
        if (StringUtils.isEmpty(product.getSKU()))
            throw new IllegalArgumentException("");
        if (StringUtils.isEmpty(product.getMainCategory()))
            throw new IllegalArgumentException("");
        if (product.getVolumeHeight() == null)
            throw new IllegalArgumentException("");
        if (product.getVolumeHeight() == null)
            throw new IllegalArgumentException("");
        if (product.getVolumeWidth() == null)
            throw new IllegalArgumentException("");
        if (product.getWeight() == null)
            throw new IllegalArgumentException("");
        haierSupplier.updateProduct(product);
    }

    @GetMapping("/manageProduct")
    public String index() {
        return "_productManage.html";
    }

    @GetMapping("/manageProductAdd")
    public String indexForCreate() {
        return "_productOperate.html";
    }

    @GetMapping("/manageProductEdit")
    public String indexForEdit(String code, Model model) {
        model.addAttribute("currentData", mainProductRepository.getOne(code));
        return "_productOperate.html";
    }

    @GetMapping("/manageProductDetail")
    public String detail(String code, Model model) {
        model.addAttribute("currentData", mainProductRepository.getOne(code));
        return "_productDetail.html";
    }

    @PostMapping("/manageProductSubmit")
    @Transactional
    public String edit(boolean createNew, String productName, String productBrand, String mainCategory
            , @RequestParam("type") String code, String SKU, BigDecimal productPrice, String unit, BigDecimal length
            , BigDecimal width, BigDecimal height, BigDecimal weight, BigDecimal serviceCharge, String productSummary
            , String productDetail) {
        MainProduct product;
        if (createNew) {
            if (StringUtils.isEmpty(code))
                throw new IllegalArgumentException("");
            if (StringUtils.isEmpty(productName))
                throw new IllegalArgumentException("");
            product = new MainProduct();
            product.setCode(code);
        } else {
            product = mainProductRepository.getOne(code);
        }

        product.setName(productName);
        product.setBrand(StringUtils.isEmpty(productBrand) ? null : productBrand);
        product.setMainCategory(StringUtils.isEmpty(mainCategory) ? null : mainCategory);
        product.setSKU(StringUtils.isEmpty(SKU) ? null : SKU);
        product.setDeposit(productPrice);
        product.setUnit(StringUtils.isEmpty(unit) ? null : unit);
        product.setVolumeLength(length);
        product.setVolumeWidth(width);
        product.setVolumeHeight(height);
        product.setWeight(weight);
        product.setInstall(serviceCharge);
        product.setDescription(StringUtils.isEmpty(productSummary) ? null : productSummary);
        product.setRichDescription(StringUtils.isEmpty(productDetail) ? null : productDetail);

        mainProductRepository.save(product);
        return "redirect:/manageProduct";
    }

    @GetMapping("/products/list")
    @RowCustom(dramatizer = JQueryDataTableDramatizer.class, distinct = true)
    public RowDefinition<MainProduct> data(final String productName, final String type) {
        return new RowDefinition<MainProduct>() {
            @Override
            public Class<MainProduct> entityClass() {
                return MainProduct.class;
            }

            @Override
            public List<Order> defaultOrder(CriteriaBuilder criteriaBuilder, Root<MainProduct> root) {
                return Arrays.asList(
                        criteriaBuilder.asc(root.get("enable"))
                        , criteriaBuilder.desc(root.get("createTime"))
                );
            }

            @Override
            public List<FieldDefinition<MainProduct>> fields() {
                return Arrays.asList(
                        Fields.asBasic("code")
                        , Fields.asBasic("brand")
                        , FieldBuilder.asName(MainProduct.class, "productName")
                                .addSelect(mainProductRoot -> mainProductRoot.get("name"))
                                .build()
                        , FieldBuilder.asName(MainProduct.class, "category")
                                .addSelect(mainProductRoot -> mainProductRoot.get("mainCategory"))
                                .build()
                        , Fields.asBasic("enable")
                        , FieldBuilder.asName(MainProduct.class, "price")
                                .addSelect(mainProductRoot -> mainProductRoot.get("deposit"))
                                .build()
                        , FieldBuilder.asName(MainProduct.class, "installFee")
                                .addSelect(mainProductRoot -> mainProductRoot.get("install"))
                                .build()

                );
            }

            @Override
            public Specification<MainProduct> specification() {
                return (root, query, cb) -> {
                    Predicate predicate = cb.conjunction();
                    if (!StringUtils.isEmpty(productName))
                        predicate = cb.and(cb.like(root.get("name"), "%" + productName + "%"));
                    if (!StringUtils.isEmpty(type))
                        predicate = cb.and(cb.like(root.get("code"), "%" + type + "%"));
                    return predicate;
                };
            }
        };
    }

}
