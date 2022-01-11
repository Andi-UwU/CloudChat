package application.utils;

import application.domain.FriendDTO;
import application.domain.Message;
import application.domain.User;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.font.PDType1Font;

import static application.utils.Constants.DATE_TIME_FORMATTER;
import static org.apache.pdfbox.pdmodel.font.Standard14Fonts.FontName.COURIER;
import static org.apache.pdfbox.pdmodel.font.Standard14Fonts.FontName.COURIER_BOLD;

public class ExporterPDF {
    private static final float NEWLINE_LEADING_SIZE =14f;           // how much space should a newline leave in a document
    private static final int LEFT_MARGIN_SIZE = 20;                 // left margin size for text
    private static final int TOP_CONTENT_MARGIN_POSITION_Y = 740;   // normal text position Y (after header)
    private static final int MARGIN_SIZE = 20;                      // page margins
    private static final int HEADER_SIZE = 11;                      // header font size
    private static final int CONTENT_FONT_SIZE = 10;                // content font size
    private static final int MAX_FIRST_MESSAGE_LINE_WIDTH = 70;
    private static final int MAX_CONTENT_WIDTH = 91;                // maximum content width on the page in font size 10 courier content string characters

    /**
     * Adds 150 test friends to test the autoNewPage function
     * @param friendList List(FriendDTO)
     */
    private void testAutoNewPage( List<FriendDTO> friendList ) {
        for (int i=1; i<=150; i++) { // adds 150 test friends to test the autoNewPage function
            FriendDTO friend = new FriendDTO(1, i + " test name", "test date");
            friendList.add(friend);
        }
    }

    /**
     * Adds 150 long messages to test the message newlines
     * @param messageList List(Message)
     */
    private void testAutoNewPageMessages( List<Message> messageList ) {
        for (int i=1; i<=150; i++) {
            messageList.add(new Message (
                    new User("testFirstName","testLastName","testUserName"),
                    Collections.singletonList(new User("testFirstName", "testLastName", "testUserName")),
                    i + " test message aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaabbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbb",
                    LocalDateTime.now()
            ) );
        }
    }

    /**
     * Saves a document to the C:/The Network/ location
     * @param document PDDocument
     * @param name String
     * @throws IOException if it is unable to save in the specified location with that name
     */
    private void saveDocument(PDDocument document, String name) throws IOException {
        if (!Files.isDirectory(Path.of("C:/The Network")))
            Files.createDirectory(Path.of("C:/The Network/"));

        document.save(new File("C:/The Network/"+name+".pdf"));
        document.close();
    }

    private PDPageContentStream pageContentStream (PDDocument document, PDPage page, int paragraphYOffset) throws IOException {
        PDPageContentStream cStream = new PDPageContentStream(document, page, PDPageContentStream.AppendMode.APPEND, false);
        cStream.setFont(new PDType1Font(COURIER),CONTENT_FONT_SIZE);
        cStream.beginText();
        cStream.newLineAtOffset(LEFT_MARGIN_SIZE, paragraphYOffset);
        cStream.setLeading(NEWLINE_LEADING_SIZE);
        return cStream;
    }

    private PDPageContentStream pageHeaderContentStream (PDDocument document, PDPage page) throws IOException {
        PDPageContentStream cStream = new PDPageContentStream(document, page, PDPageContentStream.AppendMode.OVERWRITE, false);
        cStream.setFont(new PDType1Font(COURIER_BOLD), 11); // sets the font
        cStream.beginText();
        cStream.newLineAtOffset(LEFT_MARGIN_SIZE, page.getMediaBox().getUpperRightY() - MARGIN_SIZE);  // sets the position where the paragraph starts
        cStream.setLeading(NEWLINE_LEADING_SIZE);   // sets the newline size
        return cStream;
    }

    private PDPageContentStream newAutoPageContentStream(PDDocument document) throws IOException {
        PDPage autoNewPage = new PDPage();
        document.addPage(autoNewPage);
        PDPageContentStream cStream = new PDPageContentStream(document,autoNewPage, PDPageContentStream.AppendMode.APPEND, false);
        cStream.setFont(new PDType1Font(COURIER),CONTENT_FONT_SIZE);
        cStream.beginText();
        cStream.newLineAtOffset(LEFT_MARGIN_SIZE, TOP_CONTENT_MARGIN_POSITION_Y);
        cStream.setLeading(NEWLINE_LEADING_SIZE);
        return cStream;
    }

    public void exportActivityToPDF(User currentUser, LocalDate startDate, LocalDate endDate,
                                    List<FriendDTO> friendList,     // new friends during time period
                                    List<List<Message>> messageList // all messages during time period
                                    ) throws IOException {

        PDDocument activityDocument = new PDDocument(); // create document
        PDPage first_page = new PDPage();   // create first page
        activityDocument.addPage(first_page);   // adds first page to the document

        // contentStream used to write header text on the first page
        PDPageContentStream cStream = new PDPageContentStream(activityDocument, first_page, PDPageContentStream.AppendMode.OVERWRITE, false);
        cStream.setFont(new PDType1Font(COURIER_BOLD), 11); // sets the font
        cStream.beginText();
        cStream.newLineAtOffset(LEFT_MARGIN_SIZE, first_page.getMediaBox().getUpperRightY() - MARGIN_SIZE);  // sets the position where the paragraph starts
        cStream.setLeading(NEWLINE_LEADING_SIZE);   // sets the newline size
        cStream.showText("This is the user report for " + currentUser.getFirstName() + " " +
                currentUser.getLastName() + " between " + startDate.toString() + " and " + endDate.toString());
        cStream.newLine();
        cStream.showText("New friends:");
        cStream.endText();
        cStream.close();    // closes the contextStream

        // contentStream2 used to write all friend requests
        PDPageContentStream cStream2 = new PDPageContentStream(activityDocument, first_page, PDPageContentStream.AppendMode.APPEND, false);
        cStream2.setFont(new PDType1Font(COURIER), CONTENT_FONT_SIZE);
        cStream2.beginText();
        cStream2.newLineAtOffset(LEFT_MARGIN_SIZE, TOP_CONTENT_MARGIN_POSITION_Y);    // sets where the text starts on the page
        cStream2.setLeading(NEWLINE_LEADING_SIZE);  // sets the space used by newlines
        int heightCounter = TOP_CONTENT_MARGIN_POSITION_Y;

        //testAutoNewPage(friendList); // test the autoNewPage function

        // write all new friends
        for (FriendDTO friend : friendList) {
            cStream2.newLine();
            heightCounter -= NEWLINE_LEADING_SIZE;  // moves the height counter down a new_line's size
            if (heightCounter < NEWLINE_LEADING_SIZE) { // if ran out of space on the page
                cStream2.endText();
                cStream2.close();   // closes the current contentStream
                cStream2 = newAutoPageContentStream(activityDocument);  // gets a new contentStream on a new page
                heightCounter = TOP_CONTENT_MARGIN_POSITION_Y;   // reset the height counter
            }
            cStream2.showText("     " + friend.getName() + " - " + friend.getDate());   // writes the information on the page
        }
        cStream2.endText();
        cStream2.close();   // closes the current contentStream

        /* if (messageList.size()<1) messageList.add(new ArrayList<>());
        testAutoNewPageMessages(messageList.get(0)); */

        // print all messages
        for (int i=0; i < messageList.size(); i++ ) {
            if (messageList.get(i).size()!=0) { // if conversation exists
                    // new page for a conversation
                PDPage page = new PDPage();
                activityDocument.addPage(page);

                    // write message page header
                PDPageContentStream cStream3 = pageHeaderContentStream(activityDocument,page);
                cStream3.showText("Messages with "+
                            messageList.get(i).get(0).getFrom().getFirstName()+" "+
                            messageList.get(i).get(0).getFrom().getLastName() +":");
                cStream3.endText();
                cStream3.close();

                    // write message content
                cStream3 = pageContentStream(activityDocument, page, TOP_CONTENT_MARGIN_POSITION_Y);
                heightCounter = TOP_CONTENT_MARGIN_POSITION_Y;

                for (Message message : messageList.get(i)) {
                    String text = message.getText();
                    List<String> messageRows = new ArrayList<>();
                    int currentSize=MAX_FIRST_MESSAGE_LINE_WIDTH;
                    for (int start = 0; start < text.length(); start += currentSize) {
                        messageRows.add(text.substring(start, Math.min(text.length(), start + currentSize)));
                        currentSize=MAX_CONTENT_WIDTH;
                    }

                    int newlineLeadingSize = (int) NEWLINE_LEADING_SIZE;
                    int messageLines = messageRows.size();
                    int messageVerticalSize = messageLines*newlineLeadingSize;

                    cStream3.newLine();
                    heightCounter -= messageVerticalSize;
                    if (heightCounter < NEWLINE_LEADING_SIZE) {
                        cStream3.endText();
                        cStream3.close();
                        cStream3 = newAutoPageContentStream(activityDocument);
                        heightCounter = TOP_CONTENT_MARGIN_POSITION_Y;
                    }

                    cStream3.showText("     " + message.getDate().format(DATE_TIME_FORMATTER) + " : " + messageRows.get(0));
                    for (int j=1; j<messageLines; j++) {
                        cStream3.newLine();
                        cStream3.showText("   " + messageRows.get(j));
                    }
                }
                cStream3.endText();
                cStream3.close();
            }
        }

        saveDocument(activityDocument, "Activity");
    }
}
