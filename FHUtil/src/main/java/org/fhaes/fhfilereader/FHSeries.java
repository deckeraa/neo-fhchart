package org.fhaes.fhfilereader;

public class FHSeries {

	private String title;
	private int firstYear;
	private boolean pith;
	private boolean bark;
	private boolean[] recordingYears;
	private boolean[] eventYears;
	private boolean[] injuryYears;

	/**
	 * This class is a container for data about a single series within a fire history data file.
	 * 
	 * @param title
	 * @param firstYear
	 * @param hasPith
	 * @param hasBark
	 * @param recordingYears
	 * @param eventYears
	 * @param injuryYears
	 * @throws Exception
	 */
	public FHSeries(String title, int firstYear, boolean hasPith,
			boolean hasBark, boolean[] recordingYears, boolean[] eventYears,
			boolean[] injuryYears) throws Exception {

		// Sanity checks
		if (title == null)
			throw new NullPointerException();
		if (recordingYears == null)
			throw new NullPointerException();
		if (eventYears == null)
			throw new NullPointerException();
		if (injuryYears == null)
			throw new NullPointerException();
		if (recordingYears.length != eventYears.length
				|| recordingYears.length != injuryYears.length) {
			throw new Exception(
					"Reading years, event years and injury years arrays must be the same size");
		}

		this.title = title;
		this.firstYear = firstYear;
		this.pith = hasPith;
		this.bark = hasBark;
		this.recordingYears = recordingYears;
		this.eventYears = eventYears;
		this.injuryYears = injuryYears;

	}

	/**
	 * Get the title/label for this series
	 * 
	 * @return
	 */
	public String getTitle() {
		return title;
	}

	/**
	 * Get the year for the first year in this series e.g. the year of the first ring of the sample - the pith if present.
	 * 
	 * @return
	 */
	public int getFirstYear() {
		return firstYear;
	}

	/**
	 * Get the year for the last year for this series e.g. the year of the last ring of the sample - the bark if present.
	 * 
	 * @return
	 */
	public int getLastYear() {
		return firstYear + recordingYears.length;
	}

	/**
	 * Get the number of years that this series covers
	 * 
	 * @return
	 */
	public int getLength() {
		return recordingYears.length;
	}

	/**
	 * Get a boolean[] with a size the same as the number of years this series covers. The boolean values will be false if the
	 * tree was not in 'recording status' for the year, or true if it was.
	 * 
	 * @return
	 */
	public boolean[] getRecordingYears() {
		return recordingYears;
	}

	/**
	 * Get a boolean[] with a size the same as the number of years this series covers. The boolean values will be false if the
	 * tree did not record a fire event in the year, and true if it did.
	 * 
	 * @return
	 */
	public boolean[] getEventYears() {
		return eventYears;
	}

	/**
	 * Get a boolean[] with a size the same as the number of years this series covers. The boolean values will be false if the
	 * tree did not record an injury event in the year, and true if it did.
	 * 
	 * @return
	 */
	public boolean[] getInjuryYears() {
		return injuryYears;
	}

	/**
	 * Returns a boolean indicating whether the sample had pith or not.
	 * 
	 * @return
	 */
	public boolean hasPith() {
		return pith;
	}

	/**
	 * Returns a boolean indicating whether the sample had bark or not.
	 * 
	 * @return
	 */
	public boolean hasBark() {
		return bark;
	}

}
