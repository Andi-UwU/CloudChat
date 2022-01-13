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
    private static final int MAX_FIRST_MESSAGE_LINE_WIDTH = 70;     // maximum message width on the first row
    private static final int MAX_CONTENT_WIDTH = 91;                // maximum content width on the page in content font size

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
     * Saves a document to an absolute path
     * @param document  PDDocument
     * @param name  String
     * @param absolutePath  String
     * @throws IOException if the file is unable to saved to that location
     */
    private void saveDocument(PDDocument document, String name, String absolutePath) throws IOException {
        if (!Files.isDirectory(Path.of(absolutePath)))                  // if directory does not exist
            Files.createDirectory(Path.of(absolutePath));               // create one

        document.save(new File(absolutePath+"/"+name+".pdf")); // save the file
        document.close();                                               // close the file
    }

    /**
     * Creates a contentStream for page contents
     * @param document PDDocument
     * @param page PDPage
     * @param paragraphYOffset int
     * @return contentStream
     * @throws IOException
     */
    private PDPageContentStream pageContentStream (PDDocument document, PDPage page, int paragraphYOffset) throws IOException {
        PDPageContentStream cStream = new PDPageContentStream(document, page, PDPageContentStream.AppendMode.APPEND, false);
        cStream.setFont(new PDType1Font(COURIER),CONTENT_FONT_SIZE);    // sets the font to content-size courier
        cStream.beginText();
        cStream.newLineAtOffset(LEFT_MARGIN_SIZE, paragraphYOffset);    // sets the text on a specific position on the page
        cStream.setLeading(NEWLINE_LEADING_SIZE);   // sets the newline size
        return cStream;
    }

    /**
     * Creates a contentStream for page headers
     * @param document PDDocument
     * @param page PDPage
     * @return contentStream
     * @throws IOException
     */
    private PDPageContentStream pageHeaderContentStream (PDDocument document, PDPage page) throws IOException {
        PDPageContentStream cStream = new PDPageContentStream(document, page, PDPageContentStream.AppendMode.OVERWRITE, false);
        cStream.setFont(new PDType1Font(COURIER_BOLD), HEADER_SIZE); // sets the font
        cStream.beginText();
        cStream.newLineAtOffset(LEFT_MARGIN_SIZE, page.getMediaBox().getUpperRightY() - MARGIN_SIZE);  // sets the position of the text at the top of the page
        cStream.setLeading(NEWLINE_LEADING_SIZE);   // sets the newline size
        return cStream;
    }

    /**
     * Creates a contentStream on a new page
     * @param document PDDocument
     * @return contentStream
     * @throws IOException
     */
    private PDPageContentStream newAutoPageContentStream(PDDocument document) throws IOException {
        PDPage autoNewPage = new PDPage();  // creates a new page
        document.addPage(autoNewPage);      // adds page to the document
        PDPageContentStream cStream = new PDPageContentStream(document,autoNewPage, PDPageContentStream.AppendMode.APPEND, false);
        cStream.setFont(new PDType1Font(COURIER),CONTENT_FONT_SIZE);                //
        cStream.beginText();
        cStream.newLineAtOffset(LEFT_MARGIN_SIZE, TOP_CONTENT_MARGIN_POSITION_Y);
        cStream.setLeading(NEWLINE_LEADING_SIZE);
        return cStream;
    }

    /**
     * Exports a user's activity to a PDF file
     * @param currentUser User
     * @param startDate LocalDate
     * @param endDate LocalDate
     * @param friendList List(FriendDTO)
     * @param messageList List(List(Message))
     * @param absolutePath String
     * @throws IOException
     */
    public void exportActivityToPDF(User currentUser, LocalDate startDate, LocalDate endDate,
                                    List<FriendDTO> friendList,         // new friends during time period
                                    List<List<Message>> messageList,    // all messages with different users during time period
                                    String absolutePath                 // path to save the document to
                                    ) throws IOException {

        PDDocument activityDocument = new PDDocument(); // creates the document
        PDPage first_page = new PDPage();               // create first page
        activityDocument.addPage(first_page);           // adds first page to the document

        // contentStream used to write header text on the first page
        PDPageContentStream cStream = pageHeaderContentStream(activityDocument, first_page);
        cStream.showText("This is the user report for " + currentUser.getFirstName() + " " +
                currentUser.getLastName() + " between " + startDate.toString() + " and " + endDate.toString());
        cStream.newLine();
        cStream.showText("New friends:");
        cStream.endText();
        cStream.close();    // closes the header contextStream

        //testAutoNewPage(friendList);      // test the autoNewPage function

        // contentStream2 used to write all friend requests
        PDPageContentStream cStream2 = pageContentStream(activityDocument, first_page, TOP_CONTENT_MARGIN_POSITION_Y);
        int heightCounter = TOP_CONTENT_MARGIN_POSITION_Y;

        for (FriendDTO friend : friendList) {                           // write all new friends
            cStream2.newLine();
            heightCounter -= NEWLINE_LEADING_SIZE;                      // moves the height counter down a new_line's size
            if (heightCounter < NEWLINE_LEADING_SIZE) {                 // if ran out of space on the page
                cStream2.endText();
                cStream2.close();                                       // closes the current contentStream
                cStream2 = newAutoPageContentStream(activityDocument);  // gets a new contentStream on a new page
                heightCounter = TOP_CONTENT_MARGIN_POSITION_Y;          // reset the height counter
            }
            cStream2.showText("     " + friend.getName() + " - " + friend.getDate());   // writes the information on the page
        }
        cStream2.endText();
        cStream2.close();   // closes the current contentStream

        /*
        if (messageList.size()<1) messageList.add(new ArrayList<>());
        testAutoNewPageMessages(messageList.get(0));                    // test the message auto-newline feature
        */

        // print all messages
        for (List<Message> messages : messageList) {
            if (messages.size() != 0) { // if conversation exists
                PDPage page = new PDPage();     // new page for a conversation
                activityDocument.addPage(page); // adds page to document

                // write message page header
                PDPageContentStream cStream3 = pageHeaderContentStream(activityDocument, page);
                cStream3.showText("Messages with " +
                        messages.get(0).getFrom().getFirstName() + " " +
                        messages.get(0).getFrom().getLastName() + ":");
                cStream3.endText();
                cStream3.close();

                // write message content
                cStream3 = pageContentStream(activityDocument, page, TOP_CONTENT_MARGIN_POSITION_Y);
                heightCounter = TOP_CONTENT_MARGIN_POSITION_Y;
                int newlineLeadingSize = (int) NEWLINE_LEADING_SIZE;    // sets the newline height

                for (Message message : messages) {      // for each message with a user
                    String text = message.getText();    // gets the message body
                    List<String> messageRows = new ArrayList<>();           // creates an arrayList to store the message body split into multiple rows
                    int currentSize = MAX_FIRST_MESSAGE_LINE_WIDTH;         // remembers how much width is left on the page to write
                    for (int start = 0; start < text.length(); start += currentSize) {                          // while we have enough text
                        messageRows.add(text.substring(start, Math.min(text.length(), start + currentSize)));   // splits the text into multiple rows
                        currentSize = MAX_CONTENT_WIDTH;                    // we have information on the first line, on a newline it will have the maximum page width available
                    }

                    int messageLines = messageRows.size();                      // stores how many rows a message will occupy on the page
                    int messageVerticalSize = messageLines * newlineLeadingSize;// stores how much height a message will occupy on the page

                    cStream3.newLine();
                    heightCounter -= messageVerticalSize;
                    if (heightCounter < NEWLINE_LEADING_SIZE) {                 // if there is not enough space on the page for an entire message
                        cStream3.endText();
                        cStream3.close();                                       // closes the current contentStream
                        cStream3 = newAutoPageContentStream(activityDocument);  // create a contentStream on a new page
                        heightCounter = TOP_CONTENT_MARGIN_POSITION_Y;          // reset the page height counter
                    }

                    cStream3.showText("     " + message.getDate().format(DATE_TIME_FORMATTER) + " : " + messageRows.get(0)); // prints the first line of the message
                    for (int j = 1; j < messageLines; j++) {    // prints the remaining rows
                        cStream3.newLine();
                        cStream3.showText("   " + messageRows.get(j));
                    }
                }
                cStream3.endText();
                cStream3.close();
            }
        }

        saveDocument(activityDocument, "Activity", absolutePath);   // saves the document
    }
}
