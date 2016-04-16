package com.meetrend.haopingdian.activity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.drawee.view.SimpleDraweeView;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;
import com.meetrend.haopingdian.R;
import com.meetrend.haopingdian.adatper.LeftItemBean;
import com.meetrend.haopingdian.bean.DymEditPictrueBean;
import com.meetrend.haopingdian.bean.DymPictrue;
import com.meetrend.haopingdian.bean.Pici;
import com.meetrend.haopingdian.bean.PictrueBean;
import com.meetrend.haopingdian.bean.ProductDetailBean;
import com.meetrend.haopingdian.bean.ProductDetailPicBean;
import com.meetrend.haopingdian.bean.TeaProductEntity;
import com.meetrend.haopingdian.env.CommonFinalHttp;
import com.meetrend.haopingdian.env.Server;
import com.meetrend.haopingdian.event.RefreshProductsEvent;
import com.meetrend.haopingdian.tool.Callback;
import com.meetrend.haopingdian.tool.LogUtil;
import com.meetrend.haopingdian.tool.SPUtil;
import com.meetrend.haopingdian.util.Canstant;
import com.meetrend.haopingdian.util.NumerUtil;
import com.meetrend.haopingdian.util.PxDipFormat;
import com.meetrend.haopingdian.widget.GridView;

import net.tsz.afinal.FinalActivity;
import net.tsz.afinal.FinalHttp;
import net.tsz.afinal.annotation.view.ViewInject;
import net.tsz.afinal.http.AjaxParams;

import org.json.JSONArray;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import de.greenrobot.event.EventBus;

/**
 * Created by a on 2016/3/10.
 * 商品编辑和新增
 */
public class ProductEditOrAddActivity extends  BaseActivity{

    private final static String TAG = ProductEditOrAddActivity.class.getName();

    //标题栏
    @ViewInject(id = R.id.actionbar_home, click = "finishActivity")
    ImageView backView;
    @ViewInject(id = R.id.actionbar_title)
    TextView titleView;
    @ViewInject(id = R.id.actionbar_action,click = "saveClick")
    TextView saveTextView;

    @ViewInject(id = R.id.product_name_edit)
    EditText productNameEdit;
    @ViewInject(id = R.id.product_pics_gridview)
    GridView productPicsGridView;
    @ViewInject(id = R.id.product_type_tv,click = "productTypeChooseClick")
    TextView productTypeTv;
    @ViewInject(id = R.id.product_best_low_price_edit)
    EditText productBestLowEdit;
    @ViewInject(id = R.id.product_best_higher_price_edit)
    EditText productBestHighEdit;
    @ViewInject(id = R.id.product_oriange_price_edit)
    EditText productOrirangePriceEdit;
    @ViewInject(id = R.id.product_linshou_price_edit)
    EditText productLinShouPriceEdit;
    @ViewInject(id = R.id.product_danwei_edit,click = "chooseUnitClick")
    TextView productDanWeiTv;
    //规格
    @ViewInject(id = R.id.add_product_guige_tv,click = "addProductGuiGeClick")
    TextView addProductGuiGeTv;
    @ViewInject(id = R.id.input_geige_eidt)
    EditText inputProductGuigeEdit;
    @ViewInject(id = R.id.del_product_guige_frame,click = "delProductGuigeClick")
    RelativeLayout delProductGuigeRel;
    @ViewInject(id = R.id.input_guige_linear)
    LinearLayout inputGuigeLayout;
    @ViewInject(id = R.id.input_guige_sedlinear)
    LinearLayout inputGuiGeSedLayout;


    //运费模板
    @ViewInject(id = R.id.chooseyunfeilayout)
    LinearLayout chooseyunfeiLayout;
    @ViewInject(id = R.id.chooset_yunfei_type_tv,click = "carriageClick")
    TextView carriageTv;
    @ViewInject(id = R.id.input_yunfei_linear)
    LinearLayout yunfeiLayout;
    @ViewInject(id = R.id.input_product_yunfei_edit)
    TextView inputYunFeiEdit;
    @ViewInject(id = R.id.input_product_kucun_edit)
    EditText inputProductKuncunEdit;
    @ViewInject(id = R.id.kucun_edit_above_line)
    View linview;
    @ViewInject(id = R.id.input_kucun_layout)
    LinearLayout inputKucunEditLayout;
    @ViewInject(id = R.id.choose_yunfeimoban_tv,click = "chooseYunFeiClick")
    TextView chooseYunFeiTv;
    @ViewInject(id = R.id.yunfeihintview)
    TextView yunfeihintView;


    @ViewInject(id = R.id.input_product_info_edit)
    EditText inputProductInfoEdit;
    @ViewInject(id = R.id.input_product_params_info_edit)
    EditText inputProductParamsEdit;
    @ViewInject(id = R.id.input_product_after_sell_info_edit)
    EditText inputProductafterSellEdit;

    /**
     * 编辑1;新增2
     */
    private int openType = 1;
    private String productId;
    private String productTypeId;
    private String unitId;
    private String inventoryId;

    private List<PictrueBean> pictrueList;
    private GridAdapter gridAdapter;

    private int choosePicType;//图片选择类型 1z主图，2细节图

    private PictrueBean zhuBean;
    private PictrueBean xijieBean;

    private boolean searchEditMode;//为true表示从搜索页面进入

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_edit_or_add);
        FinalActivity.initInjectedView(this);

        Intent intent = getIntent();
        openType = intent.getIntExtra(Canstant.PRODUCT_MANAGER_OPEN_TYPE,-1);

        init();
        requestIsCanModifyKucun();

        if (openType == 1){
            titleView.setText("编辑商品");
            productId = intent.getStringExtra("productId");
            boolean isCanEdit = intent.getBooleanExtra("isCanEdit",false);
            addProductGuiGeTv.setVisibility(View.GONE);
            inputGuigeLayout.setVisibility(View.VISIBLE);
            delProductGuigeRel.setVisibility(View.GONE);
            //inputProductGuigeEdit.setEnabled(false);
            yunfeiLayout.setVisibility(View.VISIBLE);
            inputYunFeiEdit.setVisibility(View.VISIBLE);
            //requestDetail(productId);

            try{
                searchEditMode = intent.getBooleanExtra("searchEditMode",false);
            }catch (Exception e){
                e.printStackTrace();
            }

            if (!isCanEdit){
                productNameEdit.setEnabled(false);
                productPicsGridView.setEnabled(false);
                productTypeTv.setEnabled(false);
                productBestHighEdit.setEnabled(false);
                productBestLowEdit.setEnabled(false);
                productDanWeiTv.setEnabled(false);
                inputProductGuigeEdit.setEnabled(false);
                inputProductInfoEdit.setEnabled(false);
                inputProductParamsEdit.setEnabled(false);
                inputProductafterSellEdit.setEnabled(false);
            }

        }else{
            titleView.setText("新增商品");
            addProductGuiGeTv.setVisibility(View.VISIBLE);
            inputGuigeLayout.setVisibility(View.GONE);
            LinearLayout.LayoutParams params = (LinearLayout.LayoutParams)chooseyunfeiLayout.getLayoutParams();
            params.topMargin = 0;

            //grid
            zhuBean = new PictrueBean();
            zhuBean.isMain =true;
            zhuBean.url = "local";
            pictrueList.add(zhuBean);
            gridAdapter = new GridAdapter();
            productPicsGridView.setAdapter(gridAdapter);
            gridAdapter.setList(pictrueList);
            gridAdapter.notifyDataSetChanged();
        }

        productPicsGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                PictrueBean pictrueBean = pictrueList.get(i);

                //主图默认图
                if (pictrueBean.isMain && pictrueBean.url.equals("local")){

                    startActivityForResult(new Intent(ProductEditOrAddActivity.this, AddPictrueActivity.class), 0x912);
                    ProductEditOrAddActivity.this.overridePendingTransition(R.anim.activity_popup, 0);
                    choosePicType = 1;
                }

                //细节默认图
                else if (pictrueBean.isMain == false && pictrueBean.url.equals("local")){

                    startActivityForResult(new Intent(ProductEditOrAddActivity.this, AddPictrueActivity.class), 0x912);
                    ProductEditOrAddActivity.this.overridePendingTransition(R.anim.activity_popup, 0);
                    choosePicType = 2;
                }

                //主图
                else if (pictrueBean.isMain == true && !pictrueBean.url.equals("local")){
                    pictrueList.remove(i);

                    //添加主图默认图
                    PictrueBean p = new PictrueBean();
                    p.isMain = true;
                    p.url="local";
                    pictrueList.add(0,p);

                    gridAdapter.notifyDataSetChanged();

                }

                //细节图
                else if (pictrueBean.isMain == false && !pictrueBean.url.equals("local")){
                    pictrueList.remove(i);

                    int num = 0;
                    for (PictrueBean p: pictrueList){
                        if (!p.isMain && p.url.equals("local"))
                            ++num;
                    }

                    //添加细节默认图
                    if (num==0){
                        PictrueBean p = new PictrueBean();
                        p.isMain = false;
                        p.url="local";
                        pictrueList.add(p);
                    }

                    //是否有细节图
                    boolean hasXiJie = false;
                    for (PictrueBean p: pictrueList){
                        if (!p.url.equals("local") && !p.isMain){
                            hasXiJie = true;
                            break;
                        }
                    }

                    //是否有默认主图
                    boolean hasDefaultZhuTu = false;
                    for (PictrueBean p: pictrueList){
                        if (p.url.equals("local") && p.isMain){
                            hasDefaultZhuTu = true;
                            break;
                        }
                    }

                    //有默认主图，但是没有细节图
                    if (!hasXiJie && hasDefaultZhuTu){
                        pictrueList.remove(pictrueList.size() -1);
                    }

                    gridAdapter.notifyDataSetChanged();
                }
            }
        });
    }


    private Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what){
                case 0:
                    requestDetail(productId);
                    break;
            }
        }
    };


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 0X111 && resultCode == 0X222){
            productTypeId = data.getStringExtra("selectId");
            productTypeTv.setText(data.getStringExtra("selectValue"));
        }else if(requestCode == 0X333 && resultCode == 0X333){
            unitId = data.getStringExtra("unitId");
            String unitName = data.getStringExtra("unitName");
            productDanWeiTv.setText(unitName);

        }else if(resultCode == RESULT_OK && requestCode == 0x912) {
            //选择图片
            String pictruePath = data.getStringExtra("path");
            if (TextUtils.isEmpty(pictruePath)) {
                showToast("没有获得图片的本地路径");
                return;
            }
            showDialog();
            //上传并刷新
            upLoadPictrue(pictruePath);
        }else if (requestCode == 0X777 && resultCode == 0X777){
            yunfeiName = data.getStringExtra("yname");
            carriageTv.setText(yunfeiName);
            if (yunfeiName.equals("固定运费")){
                yunfeiId = "0";
                yunfeiLayout.setVisibility(View.VISIBLE);
                chooseYunFeiTv.setVisibility(View.GONE);
                inputYunFeiEdit.setVisibility(View.VISIBLE);
                yunfeihintView.setText("固定运费");
                inputYunFeiEdit.setHint("请输入运费");
                inputYunFeiEdit.requestFocus();
            }else{
                yunfeiId = "1";
                yunfeiLayout.setVisibility(View.VISIBLE);
                chooseYunFeiTv.setVisibility(View.VISIBLE);
                inputYunFeiEdit.setVisibility(View.GONE);
                yunfeihintView.setText("运费模板");
                chooseYunFeiTv.setHint("请选择运费模板");
            }
        }else if(requestCode == 0X888 && resultCode == 0X888){
            yunfeiMoBanId = data.getStringExtra("yunfeimobanId");
            chooseYunFeiTv.setText(data.getStringExtra("yunfeimobanname"));
        }
    }

    private void init(){
        saveTextView.setText("保存");
        pictrueList = new ArrayList<PictrueBean>();
    }

    //保存
    public void saveClick(View view){

        if (productNameEdit.getText().toString().equals("")){
            showToast("请输入商品名称");
            return;
        }

        if (TextUtils.isEmpty(productTypeId)){
            showToast("请选择商品类型");
            return;
        }

        String highPrice = productBestHighEdit.getText().toString();
        String lowPrice = productBestLowEdit.getText().toString();

        String orangePrice = productOrirangePriceEdit.getText().toString();
        if (!NumerUtil.isInteger(orangePrice)&&!NumerUtil.isFloat(orangePrice)) {
            orangePrice = "0.00";
        }

        if (!TextUtils.isEmpty(productBestLowEdit.getText().toString())){
            if (!NumerUtil.isInteger(lowPrice)&&!NumerUtil.isFloat(lowPrice)) {
                lowPrice = "0.00";
            }
            if (Double.parseDouble(orangePrice) < Double.parseDouble(lowPrice)){
                showToast("原价不能小于最低价");
                return;
            }
        }

        if (!TextUtils.isEmpty(productBestHighEdit.getText().toString())){
            if (!NumerUtil.isInteger(highPrice)&&!NumerUtil.isFloat(highPrice)) {
                highPrice = "0.00";
            }
            if (Double.parseDouble(orangePrice) > Double.parseDouble(highPrice)){
                showToast("原价不能大于最高价");
                return;
            }
        }

        if (TextUtils.isEmpty(productLinShouPriceEdit.getText().toString())){
            showToast("请输入商品零售价");
            return;
        }

        if (!NumerUtil.isInteger(productLinShouPriceEdit.getText().toString())
                &&!NumerUtil.isFloat(productLinShouPriceEdit.getText().toString())) {
            showToast("零售价不合法");
            return;
        }



        String linPrice = productLinShouPriceEdit.getText().toString();


        //如果最高价已填写，则需判断
        if (!NumerUtil.isInteger(highPrice)&&!NumerUtil.isFloat(highPrice)) {
            highPrice = "0.00";
        }

        if (!TextUtils.isEmpty(productBestHighEdit.getText().toString())){
            if (!NumerUtil.isInteger(highPrice)&&!NumerUtil.isFloat(highPrice)) {
                highPrice = "0.00";
            }
            if (Double.parseDouble(linPrice) > Double.parseDouble(highPrice)){
                showToast("零售价不能大于最高价");
                return;
            }
        }

        //如果最底价已填写，则需判断
        if (!NumerUtil.isInteger(lowPrice)&&!NumerUtil.isFloat(lowPrice)) {
            lowPrice = "0.00";
        }

        if (!TextUtils.isEmpty(productBestLowEdit.getText().toString())){
            if (!NumerUtil.isInteger(lowPrice)&&!NumerUtil.isFloat(lowPrice)) {
                lowPrice = "0.00";
            }
            if (Double.parseDouble(linPrice) < Double.parseDouble(lowPrice)){
                showToast("零售价不能小于最低价");
                return;
            }
        }



        if (TextUtils.isEmpty(unitId)){
            showToast("请选择商品单位");
            return;
        }



        if (yunfeiId == null){
            showToast("请选择运费类型");
            return;

        }else{
            if (yunfeiId.equals("0")){
                if (inputYunFeiEdit.getText().toString().equals("")){
                    showToast("请输入固定运费");
                    return;
                }

            }else{
                if (chooseYunFeiTv.getText().toString().equals("")){
                    showToast("请选择运费模板");
                    return;
                }
            }

        }


        if (isCanModiyKucun) {
            if (TextUtils.isEmpty(inputProductKuncunEdit.getText().toString())){
                showToast("请输入商品库存");
                return;
            }
        }






        //新增或者编辑
        dealProductRequest();
    }

    //选择单位
    public void chooseUnitClick(View view){
        Intent intent = new Intent(this,UnitChooseActivity.class);
        intent.putExtra("unitId",unitId);
        startActivityForResult(intent, 0X333);
    }

    //选择运费
    public void chooseYunFeiClick(View view) {
        Intent intent = new Intent(this,ChooseYunFeiMoBanActivity.class);
        intent.putExtra("yunfeiMoBanId",yunfeiMoBanId);
        startActivityForResult(intent, 0X888);
    }

    //产品类型选择
    public void productTypeChooseClick(View view){
        Intent intent = new Intent(this,ProductTypesChooseActivity.class);
        intent.putExtra("typeId", productTypeId);
        intent.putExtra("proId", productId);
        startActivityForResult(intent, 0X111);
    }

    //添加产品规格
    public void addProductGuiGeClick(View view){
        view.setVisibility(View.GONE);
        inputGuigeLayout.setVisibility(View.VISIBLE);
        inputGuiGeSedLayout.setVisibility(View.VISIBLE);
        delProductGuigeRel.setVisibility(View.VISIBLE);
        LinearLayout.LayoutParams params = (LinearLayout.LayoutParams)chooseyunfeiLayout.getLayoutParams();
        params.topMargin = PxDipFormat.dip2px(this,20);
    }

    //删除产品规格
    public void delProductGuigeClick(View view){
        inputGuigeLayout.setVisibility(View.GONE);
        delProductGuigeRel.setVisibility(View.GONE);
        addProductGuiGeTv.setVisibility(View.VISIBLE);
        LinearLayout.LayoutParams params = (LinearLayout.LayoutParams)chooseyunfeiLayout.getLayoutParams();
        params.topMargin = 0;
    }

    private String yunfeiName ="";
    private String yunfeiId;//运费的类型
    private String yunfeiMoBanId="";//运费模板id
    //运费选择
    public void carriageClick(View view){
        Intent intent = new Intent(this,CarriageChooseTypeActivity.class);
        intent.putExtra("yname", yunfeiName);
        startActivityForResult(intent,0X777);
    }

    private void initContent(ProductDetailBean bean){
        productNameEdit.setText(bean.productName);
        if (!TextUtils.isEmpty(bean.productName))
            productNameEdit.setSelection(bean.productName.length());

        productTypeTv.setText(bean.catalogName);
        productBestLowEdit.setText(bean.minPrice);
        productBestHighEdit.setText(bean.maxPrice);
        productLinShouPriceEdit.setText(bean.offerUnitPrice);
        productOrirangePriceEdit.setText(bean.costPrice);
        productDanWeiTv.setText(bean.unitName);

        if (TextUtils.isEmpty(bean.model1Value)){
            //没有规格不可编辑
            inputProductGuigeEdit.setEnabled(false);
        }else{
            if (TextUtils.isEmpty(bean.model2Value)){
                //有一个规格可编辑
                inputProductGuigeEdit.setText(bean.model1Value);
            }else{
                //有两个规格不可编辑
                inputProductGuigeEdit.setEnabled(false);
                inputProductGuigeEdit.setText(bean.model1Value+" "+bean.model2Value);
            }
        }

        if (isCanModiyKucun){
            inputProductKuncunEdit.setText(bean.unitInventory);
        }
        inputProductInfoEdit.setText(bean.description);
        inputProductParamsEdit.setText(bean.format);
        inputProductafterSellEdit.setText(bean.service);
        inputProductafterSellEdit.setText(bean.service);

        if (bean.freightSet.equals("0")){
            carriageTv.setText("固定运费");
            inputYunFeiEdit.setText(bean.freightAmount);//运费
            chooseYunFeiTv.setVisibility(View.GONE);
            yunfeihintView.setText("固定运费");
        }else{
            carriageTv.setText("运费模板");
            yunfeiMoBanId = bean.templateId;
            chooseYunFeiTv.setVisibility(View.VISIBLE);
            inputYunFeiEdit.setVisibility(View.GONE);
            chooseYunFeiTv.setText(bean.templateName);//运费
            yunfeihintView.setText("运费模板");
        }

        productTypeId = bean.catalogId;
        unitId = bean.unitId;
        productId = bean.productId;
        inventoryId = bean.inventoryId;
        yunfeiId = bean.freightSet;


        String fengMianId = bean.avatarId;
        PictrueBean fengmianBean = new PictrueBean();
        fengmianBean.isMain = true;
        if (TextUtils.isEmpty(fengMianId)){
            fengmianBean.pictrueId = fengMianId;
            fengmianBean.url = "local";
        }else{
            fengmianBean.pictrueId = fengMianId;
            fengmianBean.url = "Ecp.Picture.view.img?pictureId="+fengmianBean.pictrueId;
        }
        pictrueList.add(fengmianBean);

        List<ProductDetailPicBean> childurls = bean.array;
        for (ProductDetailPicBean p: childurls){
           PictrueBean pictrueBean = new PictrueBean();
            pictrueBean.isMain = false;
            pictrueBean.pictrueId = p.pictureId;
            pictrueBean.url = "Ecp.Picture.view.img?pictureId="+p.pictureId;
            pictrueList.add(pictrueBean);
        }

        if (childurls.size()<=4){
            PictrueBean pictrueBean = new PictrueBean();
            pictrueBean.isMain = false;
            pictrueBean.pictrueId = "";
            pictrueBean.url = "local";
            pictrueList.add(pictrueBean);
        }

        PictrueBean pictrueBean = null;
        boolean isHasZhuTu = false;
        boolean isHasXiJieTu = false;//是否有细节图

        for (int i =0;i<pictrueList.size();i++){
            pictrueBean = pictrueList.get(i);
            if (pictrueBean.isMain && !pictrueBean.url.equals("local")){
                isHasZhuTu = true;
            }

            if (!pictrueBean.isMain && !pictrueBean.url.equals("local")){
                isHasZhuTu = true;
            }
        }

        //有主图默认图，没有细节图
        if (!isHasZhuTu && !isHasXiJieTu){
            for (int i =0;i<pictrueList.size();i++){
                pictrueBean = pictrueList.get(i);
                if (!pictrueBean.isMain && pictrueBean.url.equals("local")){
                    pictrueList.remove(i);
                    break;
                }
            }
        }

        gridAdapter = new GridAdapter();
        gridAdapter.setList(pictrueList);
        productPicsGridView.setAdapter(gridAdapter);
    }


    private void requestDetail(String productId){
        showDialog();
        AjaxParams params = new AjaxParams();
        params.put("token", SPUtil.getDefault(this).getToken());
        params.put("storeId", SPUtil.getDefault(this).getStoreId());
        params.put("productId", productId);

        Callback callback = new Callback(tag, this) {

            @Override
            public void onFailure(Throwable t, int errorNo, String strMsg) {
                super.onFailure(t, errorNo, strMsg);

                dimissDialog();
                if (null != strMsg) {
                    showToast(strMsg);
                }
            }

            @Override
            public void onSuccess(String t) {
                super.onSuccess(t);

                if (isSuccess) {
                    Gson gson = new Gson();
                    ProductDetailBean productDetailBean = gson.fromJson(data.toString(), ProductDetailBean.class);
                    //Log.i(TAG + "josn produc detail", productDetailBean.productName);
                    initContent(productDetailBean);//显示数据

                } else {
                    if (data.get("msg") != null) {
                        showToast(data.get("msg").getAsString());
                    }
                }
                dimissDialog();
            }
        };

        CommonFinalHttp finalHttp = new CommonFinalHttp();
        finalHttp.get(Server.BASE_URL + Server.GET_PRODUCT_DETAIL, params, callback);
    }


    /**
     * 更新，或者添加产品
     */
    public void dealProductRequest(){

        showDialog();
        AjaxParams params = new AjaxParams();
        params.put("token", SPUtil.getDefault(this).getToken());
        params.put("storeId", SPUtil.getDefault(this).getStoreId());

        HashMap<String,String> hashMap = new HashMap<String,String>();
        hashMap.put("productName", productNameEdit.getText().toString());
        hashMap.put("productCatalogId", productTypeId);
        hashMap.put("minPrice", productBestLowEdit.getText().toString());
        hashMap.put("maxPrice", productBestHighEdit.getText().toString());
        hashMap.put("costPrice", productOrirangePriceEdit.getText().toString());
        hashMap.put("unitPrice", productLinShouPriceEdit.getText().toString());
        hashMap.put("unitId", unitId);
        if (isCanModiyKucun){
            hashMap.put("unitInventory", inputProductKuncunEdit.getText().toString());
        }
        hashMap.put("model1Value", inputProductGuigeEdit.getText().toString());
        hashMap.put("description", inputProductInfoEdit.getText().toString());
        hashMap.put("format", inputProductParamsEdit.getText().toString());
        hashMap.put("service", inputProductafterSellEdit.getText().toString());
        hashMap.put("freightSet",yunfeiId);//0:固定运费;1:运费模版
        if (yunfeiId.equals("0")){
            hashMap.put("freightAmount",inputYunFeiEdit.getText().toString());//运费
        }else{
            hashMap.put("templateId",yunfeiMoBanId);//运费模板id
        }

        String productFengmianId = "";
        PictrueBean pictrueBean = null;

        Gson gson = new Gson();

        if (openType == 2){
            List<DymPictrue> childUrls = new ArrayList<DymPictrue>();
            for (int i=0;i<pictrueList.size();i++){
                pictrueBean = pictrueList.get(i);
                if (pictrueBean.isMain && !pictrueBean.url.equals("local")){
                    productFengmianId = pictrueBean.pictrueId;
                    Log.i("产品封面",productFengmianId);
                }

                if (!pictrueBean.isMain && !pictrueBean.url.equals("local")){
                    childUrls.add(new DymPictrue(pictrueBean.pictrueId));
                    Log.i("细节封面", pictrueBean.pictrueId);
                }
            }
            String urlsJsonArrayStr = gson.toJson(childUrls);
            Log.i("-jsonArray.toString()-", urlsJsonArrayStr);

            hashMap.put("imagePic", productFengmianId);//产品封面
            hashMap.put("pictureArray", urlsJsonArrayStr);//细节图

        }else{
            List<DymEditPictrueBean> editChildUrls = new ArrayList<DymEditPictrueBean>();
            JSONArray jsonArray = new JSONArray();
            for (int i=0;i<pictrueList.size();i++){
                pictrueBean = pictrueList.get(i);
                if (pictrueBean.isMain && !pictrueBean.url.equals("local")){
                    productFengmianId = pictrueBean.pictrueId;
                    Log.i("产品封面",productFengmianId);
                }

                if (!pictrueBean.isMain && !pictrueBean.url.equals("local")){
                    DymEditPictrueBean dymEditPictrueBean = new DymEditPictrueBean(pictrueBean.pictrueId,productId);
                    editChildUrls.add(dymEditPictrueBean);
                    Log.i("细节封面", pictrueBean.pictrueId);
                    Log.i("产品id",productId);
                }
            }

            String editUrlsJsonArrayStr = gson.toJson(editChildUrls);
            hashMap.put("productId",productId);
            hashMap.put("inventoryId",inventoryId);
            hashMap.put("imagePic", productFengmianId);//产品封面id
            hashMap.put("pictureArray", editUrlsJsonArrayStr);//细节图id
            Log.i("编辑--imagePic", productFengmianId);
            Log.i("编辑--pictureArray",editUrlsJsonArrayStr);
        }

        String json = gson.toJson(hashMap);
        params.put("args",json);

        Callback callback = new Callback(tag, this) {

            @Override
            public void onFailure(Throwable t, int errorNo, String strMsg) {
                super.onFailure(t, errorNo, strMsg);

                dimissDialog();
                if (null != strMsg) {
                    showToast(strMsg);
                }
            }

            @Override
            public void onSuccess(String t) {
                super.onSuccess(t);
                LogUtil.v(TAG, "----product---data--" + t);

                dimissDialog();
                if (isSuccess) {
                    if (openType == 2){
                        showToast("添加成功");
                        setResult(0X444, null);
                        finish();
                    }else{
                        showToast("更新成功");
                        if (searchEditMode){
                            EventBus.getDefault().post(new RefreshProductsEvent());//发送消息通知列表刷新
                            finish();
                        }else{
                            setResult(0X666, null);
                            finish();
                        }
                    }

                } else {
                    if (data.get("msg") != null) {
                        showToast(data.get("msg").getAsString());
                    }else{

                        if (openType == 2){
                            showToast("添加成功");
                        }else{
                            showToast("添加失败");
                        }
                    }
                }
            }
        };

        CommonFinalHttp finalHttp = new CommonFinalHttp();
        if(openType == 2)
            finalHttp.get(Server.BASE_URL + Server.PRODUCT_ADD, params, callback);//添加产品
        else
            finalHttp.get(Server.BASE_URL + Server.UPDATE_PRODUCT_FROM_REPONSETY, params, callback);//更新产品
    }

    private class GridAdapter extends BaseAdapter {

        private LayoutInflater layoutInflater;
        private List<PictrueBean> list;

        public GridAdapter() {
            layoutInflater = LayoutInflater.from(ProductEditOrAddActivity.this);
            list = new ArrayList<PictrueBean>();
        }

        public void setList(List<PictrueBean> list){
            this.list = list;
        }

        @Override
        public int getCount() {
            return list.size();
        }

        @Override
        public PictrueBean getItem(int position) {
            return list.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {

            ViewHolder holder = null;
            if (convertView == null) {
                holder = new ViewHolder();
                convertView = layoutInflater.inflate( R.layout.product_grid_item_layout, null);
                holder.imgView = (SimpleDraweeView) convertView.findViewById(R.id.grid_item_pic);
                holder.delIcon = (ImageView) convertView .findViewById(R.id.delimg);
                holder.hintView = (TextView) convertView.findViewById(R.id.text);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }

                PictrueBean pictrueBean = list.get(position);
                if(pictrueBean.isMain){
                    if (pictrueBean.url.equals("local")){
                        holder.imgView.setImageResource(R.drawable.zhuti);
                        holder.hintView.setVisibility(View.GONE);
                        holder.delIcon.setVisibility(View.GONE);
                    }else{

                        holder.imgView.setImageURI(Uri.parse(Server.BASE_URL + pictrueBean.url));
                        holder.hintView.setText("主图");
                        holder.hintView.setVisibility(View.VISIBLE);
                        holder.delIcon.setVisibility(View.VISIBLE);
                    }
                }else{
                    if (pictrueBean.url.equals("local")) {
                        holder.imgView.setImageResource(R.drawable.xijietu);
                        holder.hintView.setVisibility(View.GONE);
                        holder.delIcon.setVisibility(View.GONE);
                    }

                    else{
                        holder.imgView.setImageURI(Uri.parse(Server.BASE_URL + pictrueBean.url));
                        holder.hintView.setText("细节图");
                        holder.hintView.setVisibility(View.VISIBLE);
                        holder.delIcon.setVisibility(View.VISIBLE);
                    }
                }
            return convertView;
        }
    }

    /**
     * 需要上传的本地图片的路径
     * @param absPath  路径
     */
    @SuppressWarnings("unused")
    private void upLoadPictrue(String absPath){

        AjaxParams params = new AjaxParams();
        params.put("storeId", SPUtil.getDefault(this).getStoreId());

        try {

            File file = new File(absPath);
            if (null == file) {
                dimissDialog();
                showToast("没有找到图片路径");
                return;
            }

            params.put("file", file);
        } catch (FileNotFoundException e) {
            e.printStackTrace();

            LogUtil.w(tag, e.getMessage());
        }

        Callback callback = new Callback(tag, this) {

            @Override
            public void onSuccess(String t) {
                super.onSuccess(t);

                dimissDialog();
                try {
                    JSONObject jsonObject = new JSONObject(t);
                    if (isSuccess) {

                        String url = data.get("url").getAsString();
                        String pictureId = data.get("pictureId").getAsString();
                        PictrueBean pictrueBean = null;
                        if (choosePicType == 1){
                            for (int i =0;i<pictrueList.size();i++){

                                pictrueBean = pictrueList.get(i);
                                if (pictrueBean.url.equals("local") && pictrueBean.isMain)
                                    pictrueList.remove(i);

                                        pictrueBean = new PictrueBean();
                                        pictrueBean.url = url;
                                        pictrueBean.pictrueId = pictureId;
                                        pictrueBean.isMain = true;
                                        pictrueList.add(0, pictrueBean);

                                        //如果有细节默认图片则不添加
                                        int num = 0;
                                        for (PictrueBean p: pictrueList){
                                            if (!p.isMain && p.url.equals("local"))
                                                ++num;
                                        }

                                        if (num==0){
                                            pictrueBean = new PictrueBean();
                                            pictrueBean.url = "local";
                                            pictrueBean.isMain = false;
                                            pictrueList.add(pictrueBean);
                                        }

                                        gridAdapter.notifyDataSetChanged();
                                        break;
                            }
                            choosePicType = -1;
                        }else{



                            //删除默认细节主图
                            for (int i =0;i<pictrueList.size();i++){
                                pictrueBean = pictrueList.get(i);
                                if (pictrueBean.url.equals("local") && !pictrueBean.isMain){
                                    pictrueList.remove(i);
                                    break;
                                }
                            }

                            pictrueBean = new PictrueBean();
                            pictrueBean.url = url;
                            pictrueBean.pictrueId = pictureId;
                            pictrueBean.isMain = false;
                            pictrueList.add(pictrueBean);

                            if (getXijieTuNum()!= 5){
                                pictrueBean = new PictrueBean();
                                pictrueBean.url = "local";
                                pictrueBean.isMain = false;
                                pictrueList.add(pictrueBean);
                            }

                            gridAdapter.notifyDataSetChanged();
                            choosePicType = -1;
                        }
                    }else {

                        if (data.has("msg")) {
                            showToast(msg);
                        }else {
                            showToast("图片上传失败");
                        }
                    }

                } catch (Exception e) {
                    dimissDialog();
                    e.printStackTrace();
                }

            }

            @Override
            public void onFailure(Throwable t, int errorNo, String strMsg) {
                super.onFailure(t, errorNo, strMsg);

                dimissDialog();
                if (null != strMsg) {
                    showToast(strMsg);
                }else {
                    showToast("图片上传失败");
                }
            }

        };

        FinalHttp http = new FinalHttp();
        http.post(Server.BASE_URL + Server.UPLOAD_URL +
                "?token=" + SPUtil.getDefault(this).getToken(), params, callback);
    }

    private boolean isCanModiyKucun;//库存是否可以修改

    /**
     * 判断库存是否可以修改
     */
    private void requestIsCanModifyKucun(){

        showDialog();
        AjaxParams params = new AjaxParams();
        params.put("token", SPUtil.getDefault(this).getToken());
        params.put("storeId", SPUtil.getDefault(this).getStoreId());

        Callback callback = new Callback(tag, this) {

            @Override
            public void onFailure(Throwable t, int errorNo, String strMsg) {
                super.onFailure(t, errorNo, strMsg);

                dimissDialog();
                if (null != strMsg) {
                    showToast(strMsg);
                }
            }

            @Override
            public void onSuccess(String t) {
                super.onSuccess(t);

                if (isSuccess) {
                    isCanModiyKucun = data.get("result").getAsBoolean();
                    if (!isCanModiyKucun){
                        //inputProductKuncunEdit.setVisibility(View.GONE);
                        inputKucunEditLayout.setVisibility(View.GONE);
                        linview.setVisibility(View.GONE);
                    }

                    if (openType ==1)
                        handler.sendEmptyMessage(0);

                } else {
                    if (data.get("msg") != null) {
                        showToast(data.get("msg").getAsString());
                    }
                }
                dimissDialog();
            }
        };

        CommonFinalHttp finalHttp = new CommonFinalHttp();
        finalHttp.get(Server.BASE_URL + Server.JUDGE_CAN_MODIFY_KUCUN, params, callback);

    }

    /**
     * 计算有多少张细节图
     */
    public int getXijieTuNum(){
        int xijieNum =0;
        for (int i=0;i<pictrueList.size();i++){
            PictrueBean p = pictrueList.get(i);
            if (!p.isMain && !p.url.equals("local"))
                ++xijieNum;
        }
        return  xijieNum;
    }

    class ViewHolder {
        SimpleDraweeView imgView;
        ImageView delIcon;
        TextView hintView;
    }

    //结束Activity
    public void finishActivity(View view) {
        finish();
    }
}
