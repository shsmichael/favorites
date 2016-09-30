package me.icxd.bookshelve.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.HorizontalScrollView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.List;

import me.icxd.bookshelve.R;

/**
 * ViewPagerIndicator
 * Created by HaPBoy on 5/10/16.
 */
public class ViewPagerIndicator extends HorizontalScrollView {

    private Context mContext;

    private List<String> mTitles; // Receiving the passed title
    private ViewPager mViewPager; // Receiving ViewPager associated

    private LinearLayout llTabRoot; // TabLayout
    private RelativeLayout rlRoot; // Root layout
    private View vLine; // Horizontal line
    private RelativeLayout.LayoutParams lineLayoutParams;

    private int mTabVisibleCount; // The number of visible tab
    private int mSizeText; // tab label text size (sp)
    private int mColorTextNormal; // Normal font color
    private int mColorTextHighlight; // Highlight Font Color
    private int mColorLine; // Horizontal color
    private int mHeightLine; // High Line(dp)

    private static final int COUNT_DEFAULT_TAB = 4; // The default tab is visible 4
    private static final int SIZE_TEXT = 16; // The default tab label text size(sp)
    private static final int COLOR_TEXT_NORMAL = Color.parseColor("#000000"); // The default font color normal
    private static final int COLOR_TEXT_HIGHLIGHT = Color.parseColor("#FFFFFF"); // Highlight the default font color
    private static final int COLOR_LINE = Color.parseColor("#000000"); // The default horizontal color
    private static final int HEIGHT_LINE = 2; // The default line height(dp)

    public ViewPagerIndicator(Context context) {
        super(context, null);
    }

    public ViewPagerIndicator(Context context, AttributeSet attrs) {
        super(context, attrs);
        mContext = context;

        // Get XML configuration attributes
        TypedArray attributes = context.obtainStyledAttributes(attrs, R.styleable.ViewPagerIndicator);
        mTabVisibleCount = attributes.getInt(R.styleable.ViewPagerIndicator_tab_visible_count, COUNT_DEFAULT_TAB);
        if (mTabVisibleCount < 0) {
            mTabVisibleCount = COUNT_DEFAULT_TAB;
        }
        mSizeText = attributes.getInt(R.styleable.ViewPagerIndicator_tab_text_size, SIZE_TEXT);
        mColorTextNormal = attributes.getInt(R.styleable.ViewPagerIndicator_tab_text_normal_color, COLOR_TEXT_NORMAL);
        mColorTextHighlight = attributes.getInt(R.styleable.ViewPagerIndicator_tab_text_highlight_color, COLOR_TEXT_HIGHLIGHT);
        mColorLine = attributes.getInt(R.styleable.ViewPagerIndicator_tab_line_color, COLOR_LINE);
        mHeightLine = attributes.getInt(R.styleable.ViewPagerIndicator_tab_line_height, HEIGHT_LINE);
        attributes.recycle();

        initViews();
    }

    private void initViews() {
        setHorizontalScrollBarEnabled(false);

        // Root layout
        rlRoot = new RelativeLayout(mContext);
        rlRoot.setLayoutParams(new ViewGroup.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
        this.addView(rlRoot);

        // Tab Layout
        llTabRoot = new LinearLayout(mContext);
        llTabRoot.setOrientation(LinearLayout.HORIZONTAL);
        llTabRoot.setLayoutParams(new ViewGroup.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
        rlRoot.addView(llTabRoot);

        // Tab horizontal layout
        vLine = new View(mContext);
        lineLayoutParams = new RelativeLayout.LayoutParams(getScreenWidth() / mTabVisibleCount, DpToPx(mHeightLine));
        lineLayoutParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
        vLine.setLayoutParams(lineLayoutParams);
        vLine.setBackgroundColor(mColorLine);
        rlRoot.addView(vLine);
    }

    /**
     * After xml loading is complete, the callback method
     * The settings on each tab of LayoutParams
     */
    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        int childCount = llTabRoot.getChildCount();
        if (childCount == 0) {
            return;
        }

        for (int i = 0; i < childCount; i++) {
            View view = llTabRoot.getChildAt(i);
            LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) view.getLayoutParams();
            params.width = getScreenWidth() / mTabVisibleCount;
            view.setLayoutParams(params);
        }

        // 横线长度
        lineLayoutParams.width = getScreenWidth() / mTabVisibleCount;
        vLine.setLayoutParams(lineLayoutParams);
    }

    /**
     * Dynamic settings tab number
     *
     * @param count
     */
    public void setVisibleTabCount(int count) {
        mTabVisibleCount = count;
        onFinishInflate();
    }

    /**
     * Dynamic settings tab
     *
     * @param titles
     */
    public void setTabItemTitles(List<String> titles) {
        if (titles != null && titles.size() > 0) {
            llTabRoot.removeAllViews();
            mTitles = titles;
            for (String title : mTitles) {
                llTabRoot.addView(generateTextView(title));
            }
            setItemClickEvent();
        }
    }

    /**
     * Create tab according to title
     *
     * @param title
     * @return view
     */
    private View generateTextView(String title) {
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(getScreenWidth() / mTabVisibleCount, LayoutParams.MATCH_PARENT);

        TextView textView = new TextView(getContext());
        textView.setText(title);
        textView.setGravity(Gravity.CENTER);
        textView.setTextSize(TypedValue.COMPLEX_UNIT_SP, mSizeText);
        textView.setTextColor(mColorTextNormal);
        textView.setLayoutParams(params);
        return textView;
    }


    /**
     * Follow ViewPager Mobile
     *
     * @param position
     * @param positionOffset
     */
    public void scroll(int position, float positionOffset) {
        int tabWidth = getWidth() / mTabVisibleCount;
        // 当Tab处于移动至最后一个时
        // position + 1 - (mTabVisibleCount - 1) + positionOffset
        if (position >= (mTabVisibleCount - 2) && positionOffset > 0 && llTabRoot.getChildCount() > mTabVisibleCount) {
            if (mTabVisibleCount != 1) {
                scrollTo((int) ((position - (mTabVisibleCount - 2) + positionOffset) * tabWidth), 0);
            } else {
                scrollTo((int) ((position + positionOffset) * tabWidth), 0);
            }
        }

        // 移动横线
        lineLayoutParams.leftMargin = (int) ((position + positionOffset) * tabWidth);
        vLine.setLayoutParams(lineLayoutParams);
    }

    /**
     * Set associated ViewPager
     *
     * @param viewpager
     * @param position
     */
    public void setViewPager(ViewPager viewpager, int position) {
        mViewPager = viewpager;
        mViewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {

            @Override
            public void onPageSelected(int position) {
                highLightTextView(position);
            }

            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                // 滑动Tab和横线
                scroll(position, positionOffset);
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
        mViewPager.setCurrentItem(position);
        highLightTextView(position);
    }

    /**
     * Click on the tab is highlighted
     *
     * @param position
     */
    private void highLightTextView(int position) {
        resetTextViewColor();
        View view = llTabRoot.getChildAt(position);
        if (view instanceof TextView) {
            ((TextView) view).setTextColor(mColorTextHighlight);
        }
    }

    /**
     * Reset tab text color
     */
    private void resetTextViewColor() {
        for (int i = 0; i < llTabRoot.getChildCount(); i++) {
            View view = llTabRoot.getChildAt(i);
            if (view instanceof TextView) {
                ((TextView) view).setTextColor(mColorTextNormal);
            }
        }
    }

    /**
     * Setting Tab click event
     */
    private void setItemClickEvent() {
        int childCount = llTabRoot.getChildCount();
        for (int i = 0; i < childCount; i++) {
            final int j = i;
            View view = llTabRoot.getChildAt(i);
            view.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    mViewPager.setCurrentItem(j);
                }
            });
        }
    }

    /**
     * Gets the width of the screen
     *
     * @return screenWidth
     */
    private int getScreenWidth() {
        WindowManager wm = (WindowManager) getContext().getSystemService(Context.WINDOW_SERVICE);
        DisplayMetrics outMetrics = new DisplayMetrics();
        wm.getDefaultDisplay().getMetrics(outMetrics);
        return outMetrics.widthPixels;
    }

    /**
     * The DP is converted to PX
     *
     * @param dp Pixel-independent units to be converted
     * @return int
     */
    private int DpToPx(double dp) {
        float scale = getResources().getDisplayMetrics().density;
        return (int) (dp * scale + 0.5f);
    }
}
