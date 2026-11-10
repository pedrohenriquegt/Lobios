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

public class MainActivity extends Activity {
    private static final String PREFS = "lobios_local_data";
    private static final String KEY_REPORTS = "reports";
    private final int purple = Color.rgb(91, 61, 153);
    private final int darkPurple = Color.rgb(58, 39, 99);
    private final int pale = Color.rgb(246, 243, 252);
    private LinearLayout root;
    private SharedPreferences prefs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        prefs = getSharedPreferences(PREFS, MODE_PRIVATE);
        showHome();
    }

    private void setupScreen(String subtitle) {
        root = new LinearLayout(this);
        root.setOrientation(LinearLayout.VERTICAL);
        root.setBackgroundColor(pale);
        root.setPadding(dp(20), dp(18), dp(20), dp(20));

        ScrollView scroll = new ScrollView(this);
        scroll.setFillViewport(true);
        scroll.setSmoothScrollingEnabled(true);
        scroll.setVerticalScrollBarEnabled(true);
        scroll.setClipToPadding(false);
        scroll.setPadding(0, 0, 0, dp(8));
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

        scroll.addView(page, new ScrollView.LayoutParams(-1, -2));
        root.addView(scroll, new LinearLayout.LayoutParams(-1, 0, 1f));
        setContentView(root);
        currentPage = page;
        if (subtitle.equals("Nova denúncia")) currentScreen = "form";
        else if (subtitle.equals("Denúncia registrada")) currentScreen = "submitted";
        else if (subtitle.equals("Detalhes da denúncia")) currentScreen = "details";
        else currentScreen = "home";
    }

    private LinearLayout currentPage;
    private String currentScreen = "home";

    private void showHome() {
        showCollaboratorHome();
    }

    private void showCollaboratorHome() {
        setupScreen("Área do colaborador");
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
        addText(currentPage, "Conte o que aconteceu", 21, darkPurple, true);
        addText(currentPage, "O nome é opcional. Preencha apenas o que se sentir confortável em compartilhar.", 14, Color.DKGRAY, false);
        addSpace(currentPage, 12);

        addText(currentPage, "Categoria", 14, darkPurple, true);
        Spinner category = new Spinner(this);
        String[] categories = {"Selecione uma categoria", "Assédio ou discriminação", "Conflito no trabalho", "Segurança no ambiente", "Conduta inadequada", "Outro"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, categories);
        category.setAdapter(adapter);
        currentPage.addView(category, matchWrap());
        addSpace(currentPage, 12);

        addText(currentPage, "Nome (opcional)", 14, darkPurple, true);
        EditText reporterName = new EditText(this);
        reporterName.setHint("Seu nome, se quiser se identificar");
        reporterName.setSingleLine(true);
        reporterName.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_FLAG_CAP_WORDS);
        styleInput(reporterName);
        currentPage.addView(reporterName, matchWrap());
        addSpace(currentPage, 12);

        addText(currentPage, "Setor ou local (opcional)", 14, darkPurple, true);
        EditText location = new EditText(this);
        location.setHint("Ex.: setor, unidade ou local do ocorrido");
        location.setSingleLine(true);
        styleInput(location);
        currentPage.addView(location, matchWrap());
        addSpace(currentPage, 12);

        addText(currentPage, "Quando aconteceu? (opcional)", 14, darkPurple, true);
        EditText occurredAt = new EditText(this);
        occurredAt.setHint("Ex.: ontem, semana passada ou data aproximada");
        occurredAt.setSingleLine(true);
        styleInput(occurredAt);
        currentPage.addView(occurredAt, matchWrap());
        addSpace(currentPage, 12);

        addText(currentPage, "Descrição dos fatos", 14, darkPurple, true);
        EditText description = new EditText(this);
        description.setHint("Descreva a situação...");
        description.setGravity(Gravity.TOP | Gravity.START);
        description.setMinLines(5);
        description.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_FLAG_MULTI_LINE | InputType.TYPE_TEXT_FLAG_CAP_SENTENCES);
        styleInput(description);
        currentPage.addView(description, matchWrap());
        addSpace(currentPage, 12);

        addText(currentPage, "Testemunhas / informações adicionais (opcional)", 14, darkPurple, true);
        EditText witnesses = new EditText(this);
        witnesses.setHint("Inclua somente informações relevantes");
        witnesses.setMinLines(2);
        witnesses.setGravity(Gravity.TOP | Gravity.START);
        witnesses.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_FLAG_MULTI_LINE | InputType.TYPE_TEXT_FLAG_CAP_SENTENCES);
        styleInput(witnesses);
        currentPage.addView(witnesses, matchWrap());
        addSpace(currentPage, 12);

        addText(currentPage, "Providência desejada (opcional)", 14, darkPurple, true);
        EditText desiredAction = new EditText(this);
        desiredAction.setHint("Ex.: orientação, apuração ou outra providência");
        desiredAction.setMinLines(2);
        desiredAction.setGravity(Gravity.TOP | Gravity.START);
        desiredAction.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_FLAG_MULTI_LINE | InputType.TYPE_TEXT_FLAG_CAP_SENTENCES);
        styleInput(desiredAction);
        currentPage.addView(desiredAction, matchWrap());
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
                report.put("category", categories[selected]);
                report.put("name", reporterName.getText().toString().trim());
                report.put("location", location.getText().toString().trim());
                report.put("occurredAt", occurredAt.getText().toString().trim());
                report.put("description", desc);
                report.put("witnesses", witnesses.getText().toString().trim());
                report.put("desiredAction", desiredAction.getText().toString().trim());
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
        addButton(currentPage, "Copiar protocolo", v -> copyProtocol(protocol));
        addSpace(currentPage, 8);
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
        boolean fromHr = false;
        // Detail screen is reachable from either area; show RH controls by default only via a separate HR detail action.
        showDetailsScreen(report, false);
    }

    private void showDetailsScreen(JSONObject report, boolean hrMode) {
        setupScreen("Detalhes da denúncia");
        addText(currentPage, "Protocolo: " + report.optString("protocol"), 17, purple, true);
        addButton(currentPage, "Copiar protocolo", v -> copyProtocol(report.optString("protocol")));
        addSpace(currentPage, 8);
        addText(currentPage, "Categoria: " + report.optString("category"), 15, darkPurple, true);
        if (!report.optString("name").trim().isEmpty()) addText(currentPage, "Nome: " + report.optString("name"), 14, Color.DKGRAY, false);
        if (!report.optString("location").trim().isEmpty()) addText(currentPage, "Setor/local: " + report.optString("location"), 14, Color.DKGRAY, false);
        if (!report.optString("occurredAt").trim().isEmpty()) addText(currentPage, "Quando: " + report.optString("occurredAt"), 14, Color.DKGRAY, false);
        addText(currentPage, "Data do registro: " + report.optString("date"), 14, Color.DKGRAY, false);
        addSpace(currentPage, 10);
        addText(currentPage, "Descrição", 15, darkPurple, true);
        addText(currentPage, report.optString("description"), 16, Color.DKGRAY, false);
        if (!report.optString("witnesses").trim().isEmpty()) {
            addSpace(currentPage, 8);
            addText(currentPage, "Testemunhas / informações adicionais", 15, darkPurple, true);
            addText(currentPage, report.optString("witnesses"), 15, Color.DKGRAY, false);
        }
        if (!report.optString("desiredAction").trim().isEmpty()) {
            addSpace(currentPage, 8);
            addText(currentPage, "Providência desejada", 15, darkPurple, true);
            addText(currentPage, report.optString("desiredAction"), 15, Color.DKGRAY, false);
        }
        addSpace(currentPage, 12);
        addText(currentPage, "Status atual: " + report.optString("status"), 16, purple, true);
    }

    private void copyProtocol(String protocol) {
        ClipboardManager clipboard = (ClipboardManager) getSystemService(CLIPBOARD_SERVICE);
        clipboard.setPrimaryClip(ClipData.newPlainText("Protocolo Lobios", protocol));
        toast("Protocolo copiado.");
    }

    @Override
    public void onBackPressed() {
        if ("form".equals(currentScreen) || "submitted".equals(currentScreen) || "details".equals(currentScreen)) {
            showCollaboratorHome();
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
