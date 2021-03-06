package by.laguta.skryaga.service.impl;

import android.content.Context;
import android.test.AndroidTestCase;
import by.laguta.skryaga.R;
import by.laguta.skryaga.dao.ExchangeRateDao;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;
import java.io.*;

import static com.google.android.testing.mocking.AndroidMock.createMock;

public class ExchangeRateServiceImplTest extends AndroidTestCase {

    ExchangeRateServiceImpl service;

    ExchangeRateDao exchangeRateDaoMock;

    private Document document;

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        service = new ExchangeRateServiceImpl(getTestContext()) {
            @Override
            void initialiseServices() {

            }

            /*@Override
            Document getDocument() throws IOException {
                return document;
            }*/
        };
        initializeMocks();
    }

    private void initializeMocks() {
        exchangeRateDaoMock = createMock(ExchangeRateDao.class);

        service.setExchangeRateDao(exchangeRateDaoMock);
    }
//TODO: AL  fix tests
    /*@UsesMocks(ExchangeRateDao.class)
    public void testUpdateExchangeRates() throws SQLException, IOException {
        // Given
        initializeDocument(R.raw.page);

        expect(exchangeRateDaoMock.create(AndroidMock.<ExchangeRate>anyObject())).andReturn(1)
                .times(27);
        replay(exchangeRateDaoMock);

        //When
        service.updateExchangeRates();

        // Then
        verify(exchangeRateDaoMock);
    }*/

   /* @UsesMocks(ExchangeRateDao.class)
    public void testParseExchangeRates() throws SQLException, IOException {
        // Given
        initializeDocument(R.raw.page_simple);

        ExchangeRate exchangeRate = new ExchangeRate(
                null,
                new DateTime().withMillis(0),
                Currency.CurrencyType.USD,
                "АБСОЛЮТБАНК",
                "пр-т Независимости, 95",
                13740d,
                11119d);
        expect(exchangeRateDaoMock.create(exchangeRate)).andReturn(1);
        replay(exchangeRateDaoMock);

        //When
        service.updateExchangeRates();

        // Then
        verify(exchangeRateDaoMock);
    }*/

    private void initializeDocument(int page) throws IOException {
        InputStream testPage = getTestContext().getResources().openRawResource(page);
        document = Jsoup.parse(testPage, "windows-1251", getStringResource(R.string.ecopress_url));
    }

    private Context getTestContext() {
        try {
            return getContext();
        } catch (final Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    protected String getStringResource(int id) {
        return getTestContext().getResources().getString(id);
    }

    public void testParseXsl() throws TransformerException, FileNotFoundException, UnsupportedEncodingException {
        TransformerFactory tFactory = TransformerFactory.newInstance();

        Source xslDoc = new StreamSource(getTestContext().getResources().openRawResource(R.xsl.ecopress));

        InputStream testPage = getTestContext().getResources().openRawResource(R.raw.page);

        Source xmlDoc = new StreamSource(testPage);

        Transformer trasform = tFactory.newTransformer(xslDoc);

        ByteArrayOutputStream output = new ByteArrayOutputStream();

        trasform.transform(xmlDoc, new StreamResult(output));

        System.out.println(output.toString("UTF-8"));
    }

}