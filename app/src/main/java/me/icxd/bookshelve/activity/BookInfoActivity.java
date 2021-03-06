package me.icxd.bookshelve.activity;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import com.mikepenz.iconics.context.IconicsContextWrapper;

import org.litepal.crud.DataSupport;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import cn.pedant.SweetAlert.SweetAlertDialog;
import me.icxd.bookshelve.R;
import me.icxd.bookshelve.fragment.BookCoverFragment;
import me.icxd.bookshelve.fragment.BookInfoItemFragment;
import me.icxd.bookshelve.fragment.BookIntroFragment;
import me.icxd.bookshelve.fragment.BookNoteFragment;
import me.icxd.bookshelve.model.bean.Book;
import me.icxd.bookshelve.view.ViewPagerIndicator;

/**
 * Created by HaPBoy on 5/18/16.
 */
public class BookInfoActivity extends BaseActivity {

    // Context
    private Context context;

    // ViewPager
    private ViewPager viewPager;
    private FragmentPagerAdapter pagerAdapter;

    // ViewPagerIndicator
    private ViewPagerIndicator viewPagerIndicator;
    private List<String> titles = Arrays.asList("Basic Information", "Book Description", "My notes");

    // Fragment
    private List<Fragment> fragments = new ArrayList<>();

    // Book
    private Book book;

    // Favorites button image
    private int iconFavorite[] = {R.drawable.ic_favorite_border_white_24dp, R.drawable.ic_favorite_white_24dp};

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(IconicsContextWrapper.wrap(newBase));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_book_info);

        // Context
        context = this;

        // Back button
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        // Books ID

        //int bookId = getIntent().getIntExtra("id", -1);
         book =(Book) getIntent().getSerializableExtra("book");
        int  bookId =book.getId();

        // Books Obj
        //book = DataSupport.find(Book.class, bookId);

        // Activity title
        setTitle(book.getTitle());

        // ViewPager
        viewPager = (ViewPager) findViewById(R.id.view_pager);

        // ViewPagerIndicator
        viewPagerIndicator = (ViewPagerIndicator) findViewById(R.id.indicator);
        viewPagerIndicator.setTabItemTitles(titles);
        viewPagerIndicator.setVisibleTabCount(3);

        // Basic Information Fragment
       // fragments.add(BookInfoItemFragment.newInstance(bookId));
        fragments.add(BookInfoItemFragment.newInstance(book));

        // Book Description Fragment
        //fragments.add(BookIntroFragment.newInstance(bookId));
        fragments.add(BookIntroFragment.newInstance(book));

        // My notes Fragment
        fragments.add(BookNoteFragment.newInstance(book));

        // PagerAdapter
        pagerAdapter = new FragmentPagerAdapter(getSupportFragmentManager()) {
            @Override
            public int getCount() {
                return fragments.size();
            }

            @Override
            public Fragment getItem(int position) {
                return fragments.get(position);
            }
        };

        // Setting data adapter
        viewPager.setAdapter(pagerAdapter);
        viewPagerIndicator.setViewPager(viewPager, 0);

        // Book cover
        //should be sent with book as intent

        //Fragment bookCoverragment = BookCoverFragment.newInstance(bookId);
        Fragment bookCoverragment = BookCoverFragment.newInstance(book);
        getSupportFragmentManager().beginTransaction().add(R.id.fragment_book_cover, bookCoverragment).commit();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                startActivity(new Intent(this, MainActivity.class));
                return true;
            case R.id.action_favorite:
                book.setFavourite(!book.isFavourite());
                book.save();
                invalidateOptionsMenu();
                new SweetAlertDialog(context, SweetAlertDialog.SUCCESS_TYPE)
                        .setTitleText(book.isFavourite() ? "Bookmarked" : "Unfavorite")
                        .setContentText(book.isFavourite() ? "Books have been favorites" : "Favorite Books Cancelled")
                        .setConfirmText("determine")
                        .show();
                return true;
            case R.id.action_browser:
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setData(Uri.parse(book.getAlt()));
                startActivity(intent);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.book_info_menu, menu);
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        MenuItem menuItem = menu.findItem(R.id.action_favorite);
        menuItem.setIcon(iconFavorite[book.isFavourite() ? 1 : 0]);
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            startActivity(new Intent(this, MainActivity.class));
            return true;
        } else {
            return super.onKeyDown(keyCode, event);
        }
    }
}
