package br.com.lobios.app;

import android.app.Activity;
import android.app.AlertDialog;
import android.os.Bundle;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.GradientDrawable;
import android.content.SharedPreferences;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.text.InputType;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;

import org.json.JSONArray;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.UUID;
import java.util.ArrayDeque;
import java.util.Deque;

public class MainActivity extends Activity {
    private static final String PREFS = "lobios_local_data";
    private static final String KEY_REPORTS = "reports";
    private final int purple = Color.rgb(91, 61, 153);
    private final int darkPurple = Color.rgb(58, 39, 99);
    private final int pale = Color.rgb(246, 243, 252);
    private LinearLayout root;
    private SharedPreferences prefs;
    private final Deque<View> screenHistory = new ArrayDeque<>();
    private boolean restoringScreen = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        prefs = getSharedPreferences(PREFS, MODE_PRIVATE);
        showHome();
    }

    private void setupScreen(String subtitle) {
        if (!restoringScreen && root != null) screenHistory.push(root);
        root = new LinearLayout(this);
        root.setOrientation(LinearLayout.VERTICAL);
        root.setBackgroundColor(pale);
        root.setPadding(dp(20), dp(18), dp(20), dp(20));

        ScrollView scroll = new ScrollView(this);
        scroll.setFillViewport(true);
        LinearLayout page = new LinearLayout(this);
        page.setOrientation(LinearLayout.VERTICAL);
        page.setPadding(0, 0, 0, dp(16));

        TextView brand = new TextView(this);
        brand.setText("LOBIOS");
        brand.setTextColor(purple);
        brand.setTextSize(28);
        brand.setTypeface(Typeface.DEFAULT, Typeface.BOLD);
        brand.setGravity(Gravity.CENTER);
        page.addView(brand, matchWrap());

        TextView sub = new TextView(this);
        sub.setText(subtitle);
        sub.setTextColor(Color.DKGRAY);
        sub.setTextSize(14);
        sub.setGravity(Gravity.CENTER);
        sub.setPadding(0, dp(4), 0, dp(20));
        page.addView(sub, matchWrap());

        root.addView(page, new LinearLayout.LayoutParams(-1, -1));
        setContentView(root);
        currentPage = page;
    }

    private LinearLayout currentPage;

    private void showHome() {
        setupScreen("Canal de comunicação e denúncias");
        addText(currentPage, "Um espaço para se expressar", 22, darkPurple, true);
        addText(currentPage, "Registre uma denúncia ou consulte um protocolo.", 15, Color.DKGRAY, false);
        addSpace(currentPage, 18);
        addButton(currentPage, "Acessar Lobios", v -> showCollaboratorHome());
        addSpace(currentPage, 22);
        TextView note = addText(currentPage,
                "Protótipo acadêmico: os dados ficam armazenados somente neste dispositivo. Não use informações reais ou sensíveis.",
                13, Color.GRAY, false);
        note.setPadding(dp(8), dp(12), dp(8), dp(12));
    }

    private void showCollaboratorHome() {
        setupScreen("Área do colaborador");
        addSpace(currentPage, 12);
        addText(currentPage, "O que deseja fazer?", 21, darkPurple, true);
        addSpace(currentPage, 8);
        addButton(currentPage, "Registrar uma denúncia", v -> showReportForm());
        addSpace(currentPage, 10);
        addButton(currentPage, "Consultar denúncia por protocolo", v -> showLookupDialog());
        addSpace(currentPage, 20);
        addText(currentPage, "Sua denúncia não precisa conter seu nome. Evite incluir dados que identifiquem você no texto.", 14, Color.DKGRAY, false);
    }

    private void showReportForm() {
        setupScreen("Nova denúncia");
        addSpace(currentPage, 10);
        addText(currentPage, "Conte o que aconteceu", 21, darkPurple, true);
        addText(currentPage, "O nome é opcional. Descreva os fatos com clareza.", 14, Color.DKGRAY, false);
        addSpace(currentPage, 12);

        addText(currentPage, "Nome (opcional)", 14, darkPurple, true);
        EditText name = new EditText(this);
        name.setHint("Seu nome, se desejar informar");
        name.setSingleLine(true);
        name.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_FLAG_CAP_WORDS);
        styleInput(name);
        currentPage.addView(name, matchWrap());
        addSpace(currentPage, 12);

        addText(currentPage, "Categoria", 14, darkPurple, true);
        Spinner category = new Spinner(this);
        String[] categories = {"Selecione uma categoria", "Assédio ou discriminação", "Conflito no trabalho", "Segurança no ambiente", "Conduta inadequada", "Outro"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, categories);
        category.setAdapter(adapter);
        currentPage.addView(category, matchWrap());
        addSpace(currentPage, 12);

        addText(currentPage, "Descrição", 14, darkPurple, true);
        EditText description = new EditText(this);
        description.setHint("Descreva a situação...");
        description.setGravity(Gravity.TOP | Gravity.START);
        description.setMinLines(5);
        description.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_FLAG_MULTI_LINE | InputType.TYPE_TEXT_FLAG_CAP_SENTENCES);
        styleInput(description);
        currentPage.addView(description, matchWrap());
        addSpace(currentPage, 16);

        addButton(currentPage, "Enviar denúncia", v -> {
            String desc = description.getText().toString().trim();
            int selected = category.getSelectedItemPosition();
            if (selected == 0) {
                toast("Selecione uma categoria.");
                return;
            }
            if (desc.length() < 10) {
                toast("Escreva uma descrição com pelo menos 10 caracteres.");
                return;
            }
            String protocol = "LB-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase(Locale.ROOT);
            String date = new SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault()).format(new Date());
            try {
                JSONObject report = new JSONObject();
                report.put("protocol", protocol);
                report.put("name", name.getText().toString().trim());
                report.put("category", categories[selected]);
                report.put("description", desc);
                report.put("status", "Pendente");
                report.put("date", date);
                JSONArray all = loadReports();
                all.put(report);
                prefs.edit().putString(KEY_REPORTS, all.toString()).apply();
                showSubmitted(protocol);
            } catch (Exception e) {
                toast("Não foi possível salvar a denúncia.");
            }
        });
    }

    private void showSubmitted(String protocol) {
        setupScreen("Denúncia registrada");
        addText(currentPage, "Registro concluído", 23, darkPurple, true);
        addText(currentPage, "Guarde o protocolo abaixo para consultar o andamento da denúncia.", 15, Color.DKGRAY, false);
        addSpace(currentPage, 16);
        TextView code = addText(currentPage, protocol, 24, purple, true);
        code.setGravity(Gravity.CENTER);
        code.setPadding(0, dp(14), 0, dp(14));
        addButton(currentPage, "Copiar protocolo", v -> {
            ClipboardManager clipboard = (ClipboardManager) getSystemService(CLIPBOARD_SERVICE);
            clipboard.setPrimaryClip(ClipData.newPlainText("Protocolo Lobios", protocol));
            toast("Protocolo copiado.");
        });
        addSpace(currentPage, 10);
        addButton(currentPage, "Consultar esta denúncia", v -> showReportDetails(findByProtocol(protocol)));
    }

    private void showLookupDialog() {
        EditText input = new EditText(this);
        input.setHint("Ex.: LB-12AB34CD");
        input.setSingleLine(true);
        new AlertDialog.Builder(this)
                .setTitle("Consultar protocolo")
                .setMessage("Digite o protocolo recebido ao registrar a denúncia.")
                .setView(input)
                .setNegativeButton("Cancelar", null)
                .setPositiveButton("Consultar", (dialog, which) -> {
                    String protocol = input.getText().toString().trim();
                    JSONObject report = findByProtocol(protocol);
                    if (report == null) {
                        toast("Protocolo não encontrado neste dispositivo.");
                    } else {
                        showReportDetails(report);
                    }
                }).show();
    }

    private void showReportDetails(JSONObject report) {
        if (report == null) {
            toast("Denúncia não encontrada.");
            return;
        }
        showDetailsScreen(report);
    }

    private void showDetailsScreen(JSONObject report) {
        setupScreen("Detalhes da denúncia");
        addSpace(currentPage, 12);
        addText(currentPage, "Protocolo: " + report.optString("protocol"), 17, purple, true);
        addButton(currentPage, "Copiar protocolo", v -> {
            String protocol = report.optString("protocol");
            ClipboardManager clipboard = (ClipboardManager) getSystemService(CLIPBOARD_SERVICE);
            clipboard.setPrimaryClip(ClipData.newPlainText("Protocolo Lobios", protocol));
            toast("Protocolo copiado.");
        });
        addSpace(currentPage, 8);
        if (!report.optString("name", "").trim().isEmpty()) addText(currentPage, "Nome informado: " + report.optString("name"), 15, darkPurple, false);
        addText(currentPage, "Categoria: " + report.optString("category"), 15, darkPurple, true);
        addText(currentPage, "Data: " + report.optString("date"), 14, Color.DKGRAY, false);
        addSpace(currentPage, 10);
        addText(currentPage, "Descrição", 15, darkPurple, true);
        addText(currentPage, report.optString("description"), 16, Color.DKGRAY, false);
        addSpace(currentPage, 12);
        addText(currentPage, "Status atual: " + report.optString("status"), 16, purple, true);
    }


    @Override
    public void onBackPressed() {
        if (!screenHistory.isEmpty()) {
            View previous = screenHistory.pop();
            restoringScreen = true;
            setContentView(previous);
            root = (LinearLayout) previous;
            // Locate the page container so future screen transitions retain their history.
            if (root.getChildCount() > 0 && root.getChildAt(0) instanceof ScrollView) {
                ScrollView scroll = (ScrollView) root.getChildAt(0);
                if (scroll.getChildCount() > 0 && scroll.getChildAt(0) instanceof LinearLayout) currentPage = (LinearLayout) scroll.getChildAt(0);
            }
            restoringScreen = false;
        } else {
            super.onBackPressed();
        }
    }

    private JSONArray loadReports() {
        try {
            return new JSONArray(prefs.getString(KEY_REPORTS, "[]"));
        } catch (Exception e) {
            return new JSONArray();
        }
    }

    private JSONObject findByProtocol(String protocol) {
        JSONArray all = loadReports();
        for (int i = 0; i < all.length(); i++) {
            JSONObject item = all.optJSONObject(i);
            if (item != null && item.optString("protocol").equalsIgnoreCase(protocol.trim())) return item;
        }
        return null;
    }

    private void updateStatus(String protocol, String newStatus) {
        JSONArray all = loadReports();
        JSONArray updated = new JSONArray();
        for (int i = 0; i < all.length(); i++) {
            JSONObject item = all.optJSONObject(i);
            if (item == null) continue;
            if (item.optString("protocol").equalsIgnoreCase(protocol)) {
                try {
                    JSONObject copy = new JSONObject(item.toString());
                    copy.put("status", newStatus);
                    updated.put(copy);
                } catch (Exception ignored) { }
            } else updated.put(item);
        }
        prefs.edit().putString(KEY_REPORTS, updated.toString()).apply();
    }

    private LinearLayout makeCard() {
        LinearLayout card = new LinearLayout(this);
        card.setOrientation(LinearLayout.VERTICAL);
        card.setPadding(dp(14), dp(12), dp(14), dp(12));
        GradientDrawable bg = new GradientDrawable();
        bg.setColor(Color.WHITE);
        bg.setCornerRadius(dp(12));
        bg.setStroke(dp(1), Color.rgb(226, 220, 239));
        card.setBackground(bg);
        return card;
    }

    private TextView addText(LinearLayout parent, String text, int size, int color, boolean bold) {
        TextView view = new TextView(this);
        view.setText(text);
        view.setTextSize(size);
        view.setTextColor(color);
        if (bold) view.setTypeface(Typeface.DEFAULT, Typeface.BOLD);
        view.setPadding(0, dp(4), 0, dp(4));
        parent.addView(view, matchWrap());
        return view;
    }

    private void addButton(LinearLayout parent, String text, View.OnClickListener listener) {
        Button button = makeButton(text);
        button.setOnClickListener(listener);
        parent.addView(button, matchWrap());
    }

    private Button makeButton(String text) {
        Button button = new Button(this);
        button.setText(text);
        button.setAllCaps(false);
        button.setTextSize(15);
        button.setTextColor(Color.WHITE);
        GradientDrawable bg = new GradientDrawable();
        bg.setColor(purple);
        bg.setCornerRadius(dp(10));
        button.setBackground(bg);
        button.setPadding(dp(12), dp(8), dp(12), dp(8));
        return button;
    }

    private void styleInput(EditText input) {
        input.setTextSize(15);
        input.setPadding(dp(12), dp(12), dp(12), dp(12));
        input.setBackgroundColor(Color.WHITE);
    }

    private void addSpace(LinearLayout parent, int height) {
        View space = new View(this);
        parent.addView(space, new LinearLayout.LayoutParams(1, dp(height)));
    }

    private LinearLayout.LayoutParams matchWrap() {
        return new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
    }

    private int dp(int value) {
        return (int) (value * getResources().getDisplayMetrics().density + 0.5f);
    }

    private void toast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show();
    }
}
