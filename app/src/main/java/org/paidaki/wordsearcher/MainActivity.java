package org.paidaki.wordsearcher;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import org.paidaki.wordsearcher.app.WordFinder;
import org.paidaki.wordsearcher.dialogs.ErrorDialog;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static org.paidaki.wordsearcher.R.id.lvMatches;

public class MainActivity extends Activity {

    private ErrorDialog errorDialog;
    private EditText dictionary, word;
    private ViewGroup container;
    private ListView matches;
    private WordFinder wordFinder;
    private ArrayList<String> listItems;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        errorDialog = new ErrorDialog(this);
        dictionary = (EditText) findViewById(R.id.etDictionary);
        word = (EditText) findViewById(R.id.etWord);
        container = (ViewGroup) findViewById(R.id.container);
        matches = (ListView) findViewById(lvMatches);
        wordFinder = new WordFinder();
        listItems = new ArrayList<>();

        setViewEnabled(container, false);
        matches.setAdapter(new ArrayAdapter<>(this, R.layout.list_item, R.id.tvListItem, listItems));
    }

    public void openFile(View view) {
        try {
            wordFinder.loadDictionary(this, getAssets().open("greek_fixed.dic"));
        } catch (IOException e) {
            errorDialog.showAndWait("File not found.");
        }
    }

    public void searchMatches(View view) {
        String input = String.valueOf(word.getText());
        List<String> matches = wordFinder.searchWord(input);

        word.setText(wordFinder.fixWord(input));

        if (matches.isEmpty()) {
            listItems.clear();
            errorDialog.showAndWait("No matches found.");
        } else {
            listItems.clear();
            listItems.addAll(matches);
        }
    }

    public void loadDone(boolean success) {
        if (success) {
            dictionary.setText("greek_fixed.dic");
            setViewEnabled(container, true);
        } else {
            errorDialog.showAndWait("Invalid Dictionary File.");
        }
    }

    private static void setViewEnabled(View view, boolean enabled) {
        view.setEnabled(enabled);
        if (view instanceof ViewGroup) {
            ViewGroup viewGroup = (ViewGroup) view;
            for (int i = 0; i < viewGroup.getChildCount(); i++) {
                View child = viewGroup.getChildAt(i);
                setViewEnabled(child, enabled);
            }
        }
    }
}
