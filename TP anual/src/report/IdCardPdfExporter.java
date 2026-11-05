package report;

import com.google.zxing.oned.Code128Writer;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.font.PDFont;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.apache.pdfbox.pdmodel.graphics.image.PDImageXObject;

import java.awt.Color;
import java.io.File;
import java.io.IOException;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

public class IdCardPdfExporter {

    private static final float CARD_WIDTH = 243f;
    private static final float CARD_HEIGHT = 153f;
    private static final float GAP = 14f;
    private static final int COLUMNS = 2;
    private static final int ROWS = 4;
    private static final int CARDS_PER_PAGE = COLUMNS * ROWS;

    private static final float PADDING = 9f;
    private static final float HEADER_HEIGHT = 22f;
    private static final float PHOTO_WIDTH = 52f;
    private static final float PHOTO_HEIGHT = 64f;
    private static final float BARCODE_HEIGHT = 24f;
    private static final float MAX_MODULE_WIDTH = 1.4f;

    private static final Color ACCENT = new Color(37, 99, 235);
    private static final Color TEXT = new Color(15, 23, 42);
    private static final Color MUTED = new Color(100, 116, 139);
    private static final Color PHOTO_BACKGROUND = new Color(226, 232, 240);

    private static final PDFont BOLD = PDType1Font.HELVETICA_BOLD;
    private static final PDFont REGULAR = PDType1Font.HELVETICA;

    private static final DateTimeFormatter DATE = DateTimeFormatter.ofPattern("dd/MM/yyyy", Locale.US);
    private static final String[] PHOTO_EXTENSIONS = {".jpg", ".jpeg", ".png"};

    private final File photosFolder;

    public IdCardPdfExporter(File photosFolder) {
        this.photosFolder = Objects.requireNonNull(photosFolder, "The photos folder is required");
    }

    public void export(List<IdCard> cards, File output) throws IOException {
        Objects.requireNonNull(cards, "The card list is required");
        Objects.requireNonNull(output, "The output file is required");
        if (cards.isEmpty()) {
            throw new IllegalArgumentException("There are no participants to print");
        }

        try (PDDocument document = new PDDocument()) {
            for (int first = 0; first < cards.size(); first += CARDS_PER_PAGE) {
                int last = Math.min(first + CARDS_PER_PAGE, cards.size());
                addPage(document, cards.subList(first, last));
            }
            document.save(output);
        }
    }

    private void addPage(PDDocument document, List<IdCard> pageCards) throws IOException {
        PDPage page = new PDPage(PDRectangle.A4);
        document.addPage(page);

        float pageWidth = page.getMediaBox().getWidth();
        float pageHeight = page.getMediaBox().getHeight();
        float left = (pageWidth - (COLUMNS * CARD_WIDTH + (COLUMNS - 1) * GAP)) / 2;
        float top = pageHeight - (pageHeight - (ROWS * CARD_HEIGHT + (ROWS - 1) * GAP)) / 2;

        try (PDPageContentStream content = new PDPageContentStream(document, page)) {
            for (int index = 0; index < pageCards.size(); index++) {
                int column = index % COLUMNS;
                int row = index / COLUMNS;
                float x = left + column * (CARD_WIDTH + GAP);
                float y = top - (row + 1) * CARD_HEIGHT - row * GAP;
                drawCard(document, content, pageCards.get(index), x, y);
            }
        }
    }

    private void drawCard(PDDocument document, PDPageContentStream content, IdCard card, float x, float y)
            throws IOException {

        content.setStrokingColor(MUTED);
        content.setLineWidth(0.6f);
        content.addRect(x, y, CARD_WIDTH, CARD_HEIGHT);
        content.stroke();

        float headerY = y + CARD_HEIGHT - HEADER_HEIGHT;
        content.setNonStrokingColor(ACCENT);
        content.addRect(x, headerY, CARD_WIDTH, HEADER_HEIGHT);
        content.fill();
        writeText(content, BOLD, 7.5f, Color.WHITE, x + PADDING, headerY + 8, "COPA INTERNACIONAL DE CLUBES");
        String role = card.getRole().toUpperCase(Locale.ROOT);
        float roleWidth = textWidth(BOLD, 7.5f, role);
        writeText(content, BOLD, 7.5f, Color.WHITE, x + CARD_WIDTH - PADDING - roleWidth, headerY + 8, role);

        float photoX = x + PADDING;
        float photoY = headerY - PADDING - PHOTO_HEIGHT;
        drawPhoto(document, content, card, photoX, photoY);

        float textX = photoX + PHOTO_WIDTH + PADDING;
        float maxTextWidth = x + CARD_WIDTH - PADDING - textX;
        float lineY = headerY - PADDING - 9;
        writeText(content, BOLD, 9.5f, TEXT, textX, lineY, fit(BOLD, 9.5f, card.getFullName(), maxTextWidth));
        lineY -= 12;
        writeText(content, REGULAR, 7.5f, TEXT, textX, lineY, "Document: " + card.getDocument());
        lineY -= 10;
        writeText(content, REGULAR, 7.5f, TEXT, textX, lineY, "Born: " + card.getBirthDate().format(DATE));
        for (String detail : card.getDetails()) {
            lineY -= 10;
            writeText(content, REGULAR, 7.5f, MUTED, textX, lineY, fit(REGULAR, 7.5f, detail, maxTextWidth));
        }

        drawBarcode(content, card.getBarcodeValue(), x + PADDING, y + PADDING + 8, CARD_WIDTH - 2 * PADDING);
        float valueWidth = textWidth(REGULAR, 6.5f, card.getBarcodeValue());
        writeText(content, REGULAR, 6.5f, TEXT, x + (CARD_WIDTH - valueWidth) / 2, y + PADDING,
                card.getBarcodeValue());
    }

    private void drawPhoto(PDDocument document, PDPageContentStream content, IdCard card, float x, float y)
            throws IOException {
        File photo = findPhoto(card.getBarcodeValue());
        if (photo != null) {
            PDImageXObject image = PDImageXObject.createFromFile(photo.getPath(), document);
            content.drawImage(image, x, y, PHOTO_WIDTH, PHOTO_HEIGHT);
        } else {
            content.setNonStrokingColor(PHOTO_BACKGROUND);
            content.addRect(x, y, PHOTO_WIDTH, PHOTO_HEIGHT);
            content.fill();
            String initials = initialsOf(card.getFullName());
            float width = textWidth(BOLD, 16f, initials);
            writeText(content, BOLD, 16f, MUTED, x + (PHOTO_WIDTH - width) / 2, y + PHOTO_HEIGHT / 2 - 5, initials);
        }
    }

    private File findPhoto(String fileName) {
        File found = null;
        for (String extension : PHOTO_EXTENSIONS) {
            File candidate = new File(photosFolder, fileName + extension);
            if (found == null && candidate.isFile()) {
                found = candidate;
            }
        }
        return found;
    }

    private void drawBarcode(PDPageContentStream content, String value, float x, float y, float maxWidth)
            throws IOException {

        boolean[] modules = new Code128Writer().encode(value);
        float module = Math.min(MAX_MODULE_WIDTH, maxWidth / modules.length);
        float start = x + (maxWidth - modules.length * module) / 2;

        content.setNonStrokingColor(Color.BLACK);
        for (int index = 0; index < modules.length; index++) {
            if (modules[index]) {
                content.addRect(start + index * module, y, module, BARCODE_HEIGHT);
            }
        }
        content.fill();
    }

    private void writeText(PDPageContentStream content, PDFont font, float size, Color color,
                           float x, float y, String text) throws IOException {
        content.beginText();
        content.setFont(font, size);
        content.setNonStrokingColor(color);
        content.newLineAtOffset(x, y);
        content.showText(printable(font, text));
        content.endText();
    }

    private float textWidth(PDFont font, float size, String text) throws IOException {
        return font.getStringWidth(printable(font, text)) / 1000 * size;
    }

    private String fit(PDFont font, float size, String text, float maxWidth) throws IOException {
        String fitted = text;
        while (fitted.length() > 4 && textWidth(font, size, fitted) > maxWidth) {
            fitted = fitted.substring(0, fitted.length() - 4) + "...";
        }
        return fitted;
    }

    private String printable(PDFont font, String text) {
        StringBuilder printable = new StringBuilder();
        for (int index = 0; index < text.length(); index++) {
            String character = String.valueOf(text.charAt(index));
            try {
                font.encode(character);
                printable.append(character);
            } catch (IllegalArgumentException | IOException exception) {
                printable.append('?');
            }
        }
        return printable.toString();
    }

    private String initialsOf(String fullName) {
        StringBuilder initials = new StringBuilder();
        for (String word : fullName.trim().split("\\s+")) {
            if (!word.isEmpty() && initials.length() < 2) {
                initials.append(Character.toUpperCase(word.charAt(0)));
            }
        }
        return initials.toString();
    }
}