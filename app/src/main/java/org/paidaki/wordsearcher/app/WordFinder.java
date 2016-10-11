package org.paidaki.wordsearcher.app;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import org.paidaki.wordsearcher.MainActivity;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.*;

public class WordFinder {

    private HashMap<Character, ArrayList<String>> words = new HashMap<>();

    @SuppressWarnings({"Java8CollectionsApi", "Convert2streamapi"})
    public void loadDictionary(final MainActivity activity, final InputStream stream) {
        final ProgressDialog progressDialog = new ProgressDialog(activity);

        new AsyncTask<Void, Void, Boolean>() {
            @Override
            protected void onPreExecute() {
                progressDialog.setMessage("Loading dictionary...");
                progressDialog.setCanceledOnTouchOutside(false);
                progressDialog.setIndeterminate(false);
                progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
                progressDialog.show();
            }

            @Override
            protected Boolean doInBackground(Void... voids) {
                HashMap<Character, ArrayList<String>> map = new HashMap<>();

                try {
                    BufferedReader in = new BufferedReader(new InputStreamReader(stream, "UTF-8"));
                    String line = in.readLine();
                    int max, counter = 0;

                    if (line == null || line.isEmpty()) {
                        in.close();
                        return false;
                    } else {
                        max = Integer.parseInt(line);
                        line = in.readLine();
                        progressDialog.setMax(max);

                        if (line == null || line.isEmpty()) {
                            in.close();
                            return false;
                        }
                    }

                    while (line != null) {
                        progressDialog.setProgress(++counter);

                        if (!line.matches("\\p{Lu}+")) {
                            in.close();
                            return false;
                        }

                        if (map.get(line.charAt(0)) == null) {
                            map.put(line.charAt(0), new ArrayList<String>());
                        }
                        map.get(line.charAt(0)).add(line);

                        line = in.readLine();
                    }
                    in.close();
                } catch (IOException | NumberFormatException e) {
                    e.printStackTrace();
                    return false;
                }

                for (ArrayList<String> list : map.values()) {
                    Collections.sort(list);
                }
                words.clear();
                words.putAll(map);

                return true;
            }

            @Override
            protected void onPostExecute(Boolean result) {
                if (progressDialog.isShowing()) {
                    progressDialog.dismiss();
                }
                activity.loadDone(result);
            }
        }.execute();
    }

    public List<String> searchWord(String word) {
        word = fixWord(word);
        List<Character> wordChars = new ArrayList<>();

        for (char c : word.toCharArray()) {
            wordChars.add(c);
        }
        Collections.sort(wordChars);

        Set<Character> wordUniqueChars = new LinkedHashSet<>(wordChars);
        List<String> matches = new ArrayList<>();

        if (words.keySet().containsAll(wordUniqueChars)) {
            for (Character c : wordUniqueChars) {
                for (String s : words.get(c)) {
                    if (s.length() != word.length()) {
                        continue;
                    }
                    List<Character> chars = new ArrayList<>();

                    for (char cr : s.toCharArray()) {
                        chars.add(cr);
                    }
                    Collections.sort(chars);

                    if (chars.equals(wordChars)) {
                        matches.add(s);
                    }
                }
            }
        }
        Collections.sort(matches);

        return matches;
    }

    public String fixWord(String word) {
        return word
                .replaceAll("\\P{L}+", "")
                .replace((char) 0x390, (char) 0x399)
                .replace((char) 0x3B0, (char) 0x399)
                .toUpperCase()
                .replace((char) 0x386, (char) 0x391)
                .replace((char) 0x388, (char) 0x395)
                .replace((char) 0x389, (char) 0x397)
                .replace((char) 0x38A, (char) 0x399)
                .replace((char) 0x38C, (char) 0x39F)
                .replace((char) 0x38E, (char) 0x3A5)
                .replace((char) 0x38F, (char) 0x3A9)
                .replace((char) 0x3AA, (char) 0x399)
                .replace((char) 0x3AB, (char) 0x3A5);
    }
}
