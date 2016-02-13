package by.laguta.skryaga.dao.model;

import com.j256.ormlite.field.DataType;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;
import org.joda.time.DateTime;

/**
 * Author : Anatoly
 * Created : 01.02.2016 22:41
 *
 * @author Anatoly
 */
@DatabaseTable(tableName = "spending_statistics")
public class SpendingStatistics {

    @DatabaseField(generatedId = true)
    private Long id;

    public final static String DATE = "date";
    @DatabaseField(canBeNull = false, dataType = DataType.DATE_TIME, columnName = DATE)
    private DateTime date;

    public final static String MEDIAN = "median";
    @DatabaseField(canBeNull = false, columnName = MEDIAN)
    private double median;

    public final static String AVERAGE = "average";
    @DatabaseField(canBeNull = false, columnName = AVERAGE)
    private double average;

    public final static String RELATIVE = "relative";
    @DatabaseField(canBeNull = false, columnName = RELATIVE)
    private double relative;

    public SpendingStatistics() {
    }

    public SpendingStatistics(Long id, DateTime date, double median, double average) {
        this.id = id;
        this.date = date;
        this.median = median;
        this.average = average;
    }

    public SpendingStatistics(
            Long id, DateTime date, double median, double average, double relative) {
        this.id = id;
        this.date = date;
        this.median = median;
        this.average = average;
        this.relative = relative;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public DateTime getDate() {
        return date;
    }

    public void setDate(DateTime date) {
        this.date = date;
    }

    public double getMedian() {
        return median;
    }

    public void setMedian(double median) {
        this.median = median;
    }

    public double getAverage() {
        return average;
    }

    public void setAverage(double average) {
        this.average = average;
    }

    public double getRelative() {
        return relative;
    }

    public void setRelative(double relative) {
        this.relative = relative;
    }

    /**
     * Populates current objects with fields from given
     *
     * @param spendingStatistics
     */
    public void populate(SpendingStatistics spendingStatistics) {
        setMedian(spendingStatistics.getMedian());
        setAverage(spendingStatistics.getAverage());
        setRelative(spendingStatistics.getRelative());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        SpendingStatistics that = (SpendingStatistics) o;

        if (Double.compare(that.average, average) != 0) return false;
        if (Double.compare(that.median, median) != 0) return false;
        if (!date.equals(that.date)) return false;
        //noinspection RedundantIfStatement
        if (!id.equals(that.id)) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result;
        long temp;
        result = id.hashCode();
        result = 31 * result + date.hashCode();
        temp = Double.doubleToLongBits(median);
        result = 31 * result + (int) (temp ^ (temp >>> 32));
        temp = Double.doubleToLongBits(average);
        result = 31 * result + (int) (temp ^ (temp >>> 32));
        return result;
    }

    @Override
    public String toString() {
        return "SpendingStatistics{" +
                "id=" + id +
                ", date=" + date +
                ", median=" + median +
                ", average=" + average +
                '}';
    }
}
