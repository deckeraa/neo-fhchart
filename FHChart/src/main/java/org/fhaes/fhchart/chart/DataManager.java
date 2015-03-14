/*******************************************************************************
 * Copyright (C) 2013 NOAA/NCDC - Wendy Gross.
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 * 
 * Contributors:
 *     Wendy Gross
 *     Peter Brewer
 ******************************************************************************/

package org.fhaes.fhchart.chart;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

import javax.swing.JFrame;
import javax.swing.JOptionPane;

import org.jfree.data.xy.IntervalXYDataset;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * FHX Plot Data Manager. Manages the data of one FHX2 file given as input to the application.
 * 
 * @author Wendy Gross
 */
public class DataManager {

	private static final Logger log = LoggerFactory.getLogger(DataManager.class);

	// Information about the FHX data file input
	static String sentinalString = "FHX2 FORMAT";
	static String sentinalString2 = "FIRE2 FORMAT";

	private boolean sentinalStringFound = false;
	private int sentinalStringIndex = -999;    // Index of this.filearray where this.sentinalString or
	// this.sentinalString 2 is found
	private File dataFile = null;              // FHX2 data file input
	// private int dataFileLineCount; // Count of lines in this.datafile

	// private ArrayList filearray; // listing (String array) of the data file input
	List<Object> filearray;                    // Array list of the data file input
	private int startYear;                     // Oldest year of input data for the entire file
	private int endYear;                       // Youngest year of input data for the entire file
	private int numYears;                      // Number of years accounted for in input file
	private int numSamples;                    // Number of Samples/Series in the input file
	private int sampleNameSize;                // Count of characters in sample code
	private String[] sampleNames;              // Sample codes. Index zero corresponds to the first column (first
	// sample/sereis). Index one corresponds to the second column
	// (second sample/series) etc.
	private char[][] yearDominatedMatrix;      // 2-D Matrix of characters that represents
	// the actual codes of the input file traversed horizontally
	private char[][] sampleDominatedMatrix;    // 2-D Matrix of characters that reprents
	// the actual code of the input file traversed vertically

	private char[][] recorderYearMatrix;
	private FHPlotCommon fhPlotCommon = null; // Common data, methods, and static constant values for the entire application.

	// sample/series variables
	private int[] sampleStartYear;            // Array of oldest recording year for each sample
	// These values are calculated for each Sample in this.getSampleStartEndYears()
	private int[] sampleEndYear;              // Array of most recent (youngest) recording year for each sample
	// These values are calculated for each Sample/Series in this.getSampleStartEndYears()

	public String siteName = null;
	private int[] compositeAxisFilterResultSet = null;
	private int[] sampleDepthResultSet = null;
	private float[] percentScarredResultSet = null;
	private boolean silentMode = false;

	// private FHXPlotStatusWin statusWindow;

	/****************************************/
	// Constructor
	/****************************************/
	/**
	 * FHXPotDataManager constructor.
	 * 
	 * @param fhPlotCommonIn global members used by all classes of a FHX plot.
	 * @param fileIn FHX file used as input.
	 */
	DataManager(FHPlotCommon fhPlotCommonIn, File fileIn) {

		fhPlotCommon = fhPlotCommonIn;
		dataFile = fileIn;
		silentMode = fhPlotCommon.getautoExport();
		// log.debug("In FHXPlotDataManager constructor, silentMode is<" + silentMode + ">\n");
	}

	/****************************************/
	// Initialization Methods.
	/****************************************/

	/**
	 * Initializes the FHX data. Checks to make sure data is valid. Then stores data and statistics in data structures of this class.
	 * 
	 * @param none
	 */

	public boolean initialize() {

		boolean rval = true;
		if (checkDataFile())
		{
			if (getStats(this.fhPlotCommon.getfhxPlotWin().getParentFrame()))
			{
				if (getSampleNames())
				{
					if (getSampleMatrices())
					{
						if (getSampleStartEndYears())
						{
						}
						else
						{
							rval = false;
						}
					}
					else
					{
						rval = false;
					}
				}
				else
				{
					rval = false;
				}
			}
			else
			{
				rval = false;
			}
		}
		else
		{
			rval = false;
		}

		// dumpDataStructures();
		return rval;
	}

	public Boolean hasLatLong() {

		if (getLatitude() != null && getLongitude() != null)
		{
			return true;
		}
		else
		{
			return false;
		}
	}

	public Double getLatitude() {

		for (Object l : filearray)
		{
			String line = (String) l;

			if (line.startsWith("Latitude"))
			{
				String[] parts = line.split(":");
				if (parts.length == 2)
				{
					try
					{
						Double dbl = Double.valueOf(parts[1]);
						return dbl;
					}
					catch (NumberFormatException e)
					{
						return null;
					}
				}
			}
		}

		return null;
	}

	public Double getLongitude() {

		for (Object l : filearray)
		{
			String line = (String) l;

			if (line.startsWith("Longitude"))
			{
				String[] parts = line.split(":");
				if (parts.length == 2)
				{
					try
					{
						Double dbl = Double.valueOf(parts[1]);
						return dbl;
					}
					catch (NumberFormatException e)
					{
						return null;
					}
				}
			}
		}

		return null;
	}

	/**
	 * Verify that the FHX2 data file input is valid
	 * 
	 * @param none
	 */

	private boolean checkDataFile() {

		boolean rval = true;
		BufferedReader in;
		String line;
		int counter = 0;

		sentinalStringFound = false;
		sentinalStringIndex = -999;
		// dataFileLineCount = 0;

		try
		{
			filearray = new ArrayList<Object>();
			// filearray = new ArrayList();
			in = new BufferedReader(new FileReader(dataFile));
			counter = 0;
			while ((line = in.readLine()) != null)
			{
				if (rval = filearray.add(line))
				{
					if (line.startsWith(sentinalString) || line.startsWith(sentinalString2))
					{
						sentinalStringIndex = counter + 1;
						sentinalStringFound = true;
					}
					if (line.startsWith("Name of site") == true)
					{
						int lastIndexOfColon = line.indexOf(":");
						if (lastIndexOfColon != -1)
						{
							this.siteName = line.substring(lastIndexOfColon + 1, line.length());
							this.siteName = this.siteName.trim();
						}
					}
					counter++;
				}
				else
				{
					break;
				}
			}
			in.close();
			// dataFileLineCount = counter;
			rval = sentinalStringFound;
		}
		catch (IOException e)
		{
			rval = false;
		}

		if (rval == false)
		{
			// FHX2 file format is invalid
			String strng = "Sorry, " + dataFile.getName() + " is NOT a valid FHX2 file.";
			if (silentMode == false)
			{
				JOptionPane.showMessageDialog(null, strng, "Alert", JOptionPane.ERROR_MESSAGE);
			}
			else
			{
				log.debug(strng);
			}
		}
		// log.debug("Exit checkFile, rval<" + rval + ">sentinalStringIndex<" + sentinalStringIndex + ">numlines<" + dataFileLineCount +
// ">\n");
		return rval;
	}

	@SuppressWarnings("unused")
	private boolean getStats() {

		return this.getStats(null);
	}

	/**
	 * Get statistics for the FHX data: start year, end year, number samples, sample name size, Then stores data and statistics in data
	 * structures of this class. If necessary, End-Of-File character is adjusted for
	 * 
	 * @param none
	 */
	@SuppressWarnings("unused")
	private boolean getStats(JFrame parent) {

		boolean rval = true;
		String tmpString;
		Integer tmpInteger;
		tmpString = (String) this.filearray.get(this.sentinalStringIndex);
		// log.debug("String to be tokenized<" + tmpString + ">\n");
		StringTokenizer st = new StringTokenizer(tmpString);
		if (st.countTokens() >= 3)
		{
			tmpString = st.nextToken();
			// log.debug("nextToken<" + tmpString + ">\n");
			tmpInteger = new Integer(tmpString);
			startYear = tmpInteger.intValue();
			tmpString = st.nextToken();
			// log.debug("nextToken<" + tmpString + ">\n");
			tmpInteger = new Integer(tmpString);
			numSamples = tmpInteger.intValue();
			tmpString = st.nextToken();
			// log.debug("nextToken<" + tmpString + ">\n");
			tmpInteger = new Integer(tmpString);
			sampleNameSize = tmpInteger.intValue();

			endYear = startYear + (filearray.size() - (sentinalStringIndex + sampleNameSize + 3));
			numYears = endYear - startYear + 1;
		}
		else
		{
			// Format of file is invalid.
			rval = false;
		}

		// If necessary, adjust for EOF character on last line of FHX file
		// If the lenght of the last line of the file is less than the number
		// of samples(series), remove it from filearray, and adjust endYear and
		// numYears.
		String tmpString2 = (String) this.filearray.get(this.filearray.size() - 1);
		if (tmpString2.length() < numSamples)
		{
			this.filearray.remove(this.filearray.size() - 1);
			this.endYear = this.endYear - 1;
			this.numYears--;
		}

		if (rval)
		{
			// FHX2 file format is valid - make sure user still wants to create a plot.
			String strng = "The file '" + dataFile.getName() + "' contains: \n  Number of Series - " + numSamples + "\n  Earliest Year - "
					+ startYear + "\n  Latest Year - " + endYear + "\n\nWould you like to create the graph?\n";
			String titleStrng = "Valid File";
			String[] btnText = new String[2];
			btnText[0] = "OK";
			btnText[1] = "Cancel";
			Object[] btnTextObj = new Object[2];
			btnTextObj[0] = (Object) btnText[0];
			btnTextObj[1] = (Object) btnText[1];

			if (silentMode == false)
			{
				/*
				 * int n = JOptionPane.showOptionDialog( parent, (Object)strng, titleStrng, JOptionPane.YES_NO_OPTION,
				 * JOptionPane.QUESTION_MESSAGE, null, btnTextObj, btnTextObj[0]);
				 * 
				 * if (n == JOptionPane.YES_NO_OPTION) { rval = true; } else { rval = false; }
				 */
			}
			else
			{
				// log.debugstrng);
			}
		}
		/*
		 * String strng = dataFile.getName() + " contains: \n    Number of Series " + numSamples + "\n    Earliest Year " + startYear +
		 * "\n    Latest Year " + endYear + "\n\nClick OK to Plot"; JOptionPane.showMessageDialog (null, strng, new
		 * String(dataFile.getName() + ": is a vaild FHX2 file"), JOptionPane.INFORMATION_MESSAGE); }
		 */

		// log.debug("Exit getStats, rval<" + rval + ">startYear<" + startYear + ">numSamples<" + numSamples + ">sampleNameSize<" +
// sampleNameSize + ">endYear<" + endYear + ">\n");
		// log.debug("filearray.size()<" + filearray.size() + ">sentinalStringIndex<" + sentinalStringIndex + ">sampleNameSize<" +
// sampleNameSize + ">sampleNameSize<" + sampleNameSize + ">\n");
		return rval;
	}

	/**
	 * Get the Sample codes from the FHX2 file and store them in this.sampleNames
	 * 
	 * @param none
	 */

	private boolean getSampleNames() {

		boolean rval = true;
		char[][] names;
		String tmpString;
		int index;

		this.sampleNames = new String[numSamples];
		names = new char[numSamples][sampleNameSize];
		index = sentinalStringIndex + 1;
		for (int i = 0; i < sampleNameSize; i++)
		{
			tmpString = (String) filearray.get(index);
			// log.debug("In loop, index<" + index + ">tmpString<" + tmpString + ">\n");

			for (int j = 0; j < numSamples; j++)
			{
				names[j][i] = ' ';
			}

			int numchars = Math.min(numSamples, tmpString.length());
			for (int j = 0; j < numchars; j++)
			{
				char ch = tmpString.charAt(j);
				// log.debug("j<" + j + ">ch<" + ch + ">\n");
				names[j][i] = ch;
			}
			index++;
		}

		for (int i = 0; i < numSamples; i++)
		{
			sampleNames[i] = new String(names[i]);
		}

		return rval;
	}

	private boolean getSampleMatrices() {

		boolean rval = true;
		String tmpString;
		int count;
		int index = sentinalStringIndex + sampleNameSize + 2;
		// log.debug("Enter getSampleMatrices, index<" + index + ">\n");

		this.sampleDominatedMatrix = new char[numSamples][endYear - startYear + 1];
		count = index;
		for (int i = 0; i < numYears; i++)
		{
			tmpString = (String) filearray.get(count);
			// log.debug("filearrayline<" + tmpString + ">\n");

			for (int j = 0; (j < numSamples) && (j < tmpString.length()); j++)
			{
				// for (int j = 0; j < tmpString.length(); j++) {
				sampleDominatedMatrix[j][i] = tmpString.charAt(j);
			}
			count++;
		}

		// Substitute begining and ending dots with blanks
		for (int i = 0; i < this.numSamples; i++)
		{
			for (int j = 0; j < this.numYears; j++)
			{
				if (sampleDominatedMatrix[i][j] == '.')
				{
					sampleDominatedMatrix[i][j] = ' ';
				}
				else
				{
					// Non '.' character found. Stop substitutions. Exit loop.
					break;
				}
			}
			for (int j = this.numYears - 1; j >= 0; j--)
			{
				if (sampleDominatedMatrix[i][j] == '.')
				{
					sampleDominatedMatrix[i][j] = ' ';
				}
				else
				{
					// Non '.' character found. Stop substitutions. Exit loop.
					break;
				}
			}
		}

		// Create year dominated matrix
		yearDominatedMatrix = new char[numYears][numSamples];
		recorderYearMatrix = new char[numYears][numSamples];
		for (int i = 0; i < numYears; i++)
		{
			for (int j = 0; j < numSamples; j++)
			{
				yearDominatedMatrix[i][j] = sampleDominatedMatrix[j][i];
				recorderYearMatrix[i][j] = 'x';
			}
		}

		return rval;
	}

	private boolean getSampleStartEndYears() {

		boolean rval = true;
		this.sampleStartYear = new int[this.numSamples];
		this.sampleEndYear = new int[this.numSamples];

		for (int i = 0; i < this.numSamples; i++)
		{
			for (int j = this.startYear; j <= this.endYear; j++)
			{
				if (isSampleYearRecorderYear(j, i) == true)
				{
					sampleStartYear[i] = j;
					break;
				}
			}
			for (int j = this.endYear; j >= this.startYear; j--)
			{
				if (isSampleYearRecorderYear(j, i) == true)
				{
					sampleEndYear[i] = j;
					break;
				}
			}
		}
		return rval;
	}

	/*********** Finish of Initialization Methods ***********/

	/****************************************/
	// Methods that get codes and types.
	/****************************************/

	private String getCode(int year, int sampleId) {

		String rtnCode = null;
		char tmpChar; // Temporary storage for a character
		char[] codeChar; // Code at the year of the sample input
		codeChar = new char[1]; // Allocate storage for the code.

		// Get the code
		tmpChar = yearDominatedMatrix[year - startYear][sampleId];
		codeChar[0] = tmpChar;
		rtnCode = new String(codeChar);
		return rtnCode;
	}

	private boolean isFireEventCode(int year, int sampleId) {

		boolean rval = false;
		if (fhPlotCommon.getfireEventCodes().indexOf(getCode(year, sampleId)) != -1)
		{
			rval = true;
		}
		return rval;
	}

	private boolean isInjuryCode(int year, int sampleId) {

		boolean rval = false;
		if (fhPlotCommon.getinjuryCodes().indexOf(getCode(year, sampleId)) != -1)
		{
			rval = true;
		}
		return rval;
	}

	/*
	 * private boolean isRecorderCode(int year, int sampleId) { boolean rval = false; if
	 * (fhPlotCommon.getrecorderCodes().indexOf(getCode(year, sampleId)) != -1) { rval = true; } return rval; }
	 * 
	 * private boolean isNonrecorderCode(int year, int sampleId) { boolean rval = false; if
	 * (fhPlotCommon.getnonrecorderCodes().indexOf(getCode(year, sampleId)) != -1) { rval = true; } return rval; }
	 * 
	 * private boolean isInnerCode(int year, int sampleId) { boolean rval = false; if (fhPlotCommon.getinnerCodes().indexOf(getCode(year,
	 * sampleId)) != -1) { rval = true; } return rval; }
	 * 
	 * private boolean isOuterCode(int year, int sampleId) { boolean rval = false; if (fhPlotCommon.getouterCodes().indexOf(getCode(year,
	 * sampleId)) != -1) { rval = true; } return rval; }
	 * 
	 * private boolean isPithCode(int year, int sampleId) { boolean rval = false; if (fhPlotCommon.getpithCodes().indexOf(getCode(year,
	 * sampleId)) != -1) { rval = true; } return rval; }
	 * 
	 * private boolean isBarkCode(int year, int sampleId) { boolean rval = false; if (fhPlotCommon.getbarkCodes().indexOf(getCode(year,
	 * sampleId)) != -1) { rval = true; } return rval; }
	 */

	/************* Finish of methods that get codes and types. **************/

	/****************************************/
	// Methods used for Filtering and Analysis.
	/****************************************/

	/**
	 * countScarredIsMinimumOf. Given a specific year and given a set of samples curently being used for analysis, DETERMINE THE COUNT
	 * SCARRED IS A MINIMUM VALUE This count includes all Fire Events
	 * 
	 * @param year, year to be determined.
	 * @param minimumCount, minimum count to be met.
	 */

	/*
	 * private boolean countScarredIsMinimumOf(int year, int minimumCount) { boolean rval = false; int count = 0;
	 * 
	 * for (int i = 0; i < this.numSamples; i++) { // if in Analysis Sample set if (isFHX2ColumnBeingPlotted(i + 1)) { if
	 * (isFireEventCode(year, i) == true) { count++; if (count == minimumCount) { rval = true; break; } } } } return rval; }
	 */

	/**
	 * Given a specific year and given a set of samples curently being used for analysis, DETERMINE THE COUNT SCARRED (INCLUDING INJURIES IF
	 * FLAG IS TRUE). This count includes all Fire Events and also includes injuries (that are recorder years), if other injuries is true
	 * 
	 * @param year, year to be determined.
	 */
	private int countScarred(int year, boolean otherInjuries) {

		int rval = 0;
		for (int i = 0; i < this.numSamples; i++)
		{
			// if in Analysis Sample set
			if (isFHX2ColumnBeingPlotted(i + 1))
			{
				if (isSampleYearRecorderYear(year, i) == true)
				{
					if (isFireEventCode(year, i) == true)
					{
						rval++;
						continue;
					}
					if (isInjuryCode(year, i) == true)
					{
						if (otherInjuries == true)
						{
							rval++;
							continue;
						}
					}
				}
				else
				{
					// This year for this sample is not a recorder year.
					// Do not count it.
					continue;
				}
			}
		}
		return rval;
	}

	/**
	 * Given a specific year and given a set of samples, DETERMINE COUNT OF RECORDING YEARS, THE SAMPLE DEPTH This count includes all
	 * recording years. It includes all Fire Events, recorder years and also includes injuries (that are recorder years)
	 * 
	 * @param year, year to be determined.
	 */
	private int sampleDepth(int year) {

		int rval = 0;
		for (int i = 0; i < this.numSamples; i++)
		{
			if (isFHX2ColumnBeingPlotted(i + 1))
			{
				if (isSampleYearRecorderYear(year, i) == true)
				{
					rval++;
				}
				else
				{
					// This year for this sample is not a recording year.
					// Do not count it.
				}
			}
		}
		return rval;
	}

	/**
	 * Determine if a year of a given sample is a recorder year
	 * 
	 * @param year year to be determined.
	 * @param sampleId identifier of the sample.
	 */
	@SuppressWarnings("unused")
	private boolean isSampleYearRecorderYear(int year, int sampleId) {

		boolean rval = false;
		char tmpChar; // Temporary storage for a character
		char[] codeInQuestion; // code at the year of the sample input
		char[] compareCode;    // code of on of the years previous to codeInQuestion
		boolean codeFoundFlag; // flag that indicates that a compareCode which denotes
		// recorder, nonrecorder has been found
		codeInQuestion = new char[1];
		// String codeString = null;
		compareCode = new char[1];
		// String compareString = null;

		boolean answerFoundInMatrix;

		// Is answer already stored in this.recorderYearMatrix?
		// If so, use it.
		answerFoundInMatrix = false; // Assume answer is not in the matrix.
		char cval = this.recorderYearMatrix[year - startYear][sampleId];
		if (cval != 'x')
		{
			if (cval == '1')
			{
				rval = true;
				answerFoundInMatrix = true;
			}
			else if (cval == '0')
			{
				rval = false;
				answerFoundInMatrix = true;
			}
		}
		if (answerFoundInMatrix == true)
		{
			return rval;
		}

		// It has not been determined yet if the code in question is a recorder year.
		// Get the code in question
		tmpChar = yearDominatedMatrix[year - startYear][sampleId];
		codeInQuestion[0] = tmpChar;

		// Evaluate the code in question
		if (fhPlotCommon.getrecorderCodes().indexOf(new String(codeInQuestion)) != -1)
		{
			// codeInQuestion is one of the recorder codes: "|".
			// Recorder codes ALWAYS denote a recorder year.
			rval = true;
		}
		else if (fhPlotCommon.getfireEventCodes().indexOf(new String(codeInQuestion)) != -1)
		{
			// codeInQuestion is one of the fire event codes "DEMLAU"
			// Fire codes ALWAYS denote a recorder year.
			rval = true;
		}
		else if (fhPlotCommon.getnonrecorderCodes().indexOf(new String(codeInQuestion)) != -1)
		{
			// codeInQuestion is one of the non-recorder codes: "."
			// Non-recorder codes NEVER denote a recorder year.
			rval = false;
		}
		else if (fhPlotCommon.getpithCodes().indexOf(new String(codeInQuestion)) != -1)
		{
			// codeInQuestion is one of the pith codes: "["
			// Pith codes NEVER denote a recorder year.
			rval = false;
		}
		else if (fhPlotCommon.getinnerCodes().indexOf(new String(codeInQuestion)) != -1)
		{
			// codeInQuestion is one of the inner codes: "{"
			rval = false;
			// Inner codes NEVER denote a recorder year, UNLESS it is followed by |.
			int nextYear = year - startYear + 1;
			// char tmpChar2 = yearDominatedMatrix[year-startYear][sampleId];
			if (nextYear <= endYear)
			{
				if (yearDominatedMatrix[nextYear][sampleId] == '|')
				{
					rval = true;
				}
			}
		}
		else if ((fhPlotCommon.getouterCodes().indexOf(new String(codeInQuestion)) != -1)
				|| (fhPlotCommon.getbarkCodes().indexOf(new String(codeInQuestion)) != -1)
				|| (fhPlotCommon.getinjuryCodes().indexOf(new String(codeInQuestion)) != -1))
		{
			// codeInQuestion is one of the outer codes: "}", bark codes: "]", or injury codes: "demlau"
			// Determine if the code in question is a recorder or non-recorder year
			// by assessing the codes that preceed it.
			codeFoundFlag = false;
			for (int i = (numYears) - (endYear - year) - 1, currentYear = year - 1; i >= 0; i--, currentYear--)
			{
				compareCode[0] = yearDominatedMatrix[i][sampleId];
				if ((fhPlotCommon.getrecorderCodes().indexOf(new String(compareCode)) != -1)
						|| (fhPlotCommon.getnonrecorderCodes().indexOf(new String(compareCode)) != -1)
						|| (fhPlotCommon.getfireEventCodes().indexOf(new String(compareCode)) != -1))
				{
					codeFoundFlag = true; // codeInQuestion equals compareCode[0];
					break;
				}
			}
			if (fhPlotCommon.getnonrecorderCodes().indexOf(new String(compareCode)) != -1)
			{
				// A non-recorder year has been found most recently previous to the year of the
				// codeIn question. compareCode is one of the nonrecorder codes: "."
				rval = false;
			}
			else if ((fhPlotCommon.getrecorderCodes().indexOf(new String(compareCode)) != -1)
					|| (fhPlotCommon.getfireEventCodes().indexOf(new String(compareCode)) != -1))
			{
				// A recorder year has been found most recently previous to the year of the
				// codeInQuestion. compareCode is one of the recorder codes: "|", or one of the
				// fireEventCodes: "DEMLAU"
				rval = true;
			}
			else if (codeFoundFlag == false)
			{
				// Can't find an answer in the years previous to the codeInQuestion.
				rval = false;
			}
		}

		// Update this.recorderYearMatrix
		if (rval == true)
		{
			this.recorderYearMatrix[year - startYear][sampleId] = '1';
		}
		else
		{
			this.recorderYearMatrix[year - startYear][sampleId] = '0';
		}

		return rval;
	}

	public void clearCompositeAxisFilterResultSet() {

		this.compositeAxisFilterResultSet = null;
	}

	public int[] getcompositeAxisFilterResultSet() {

		return this.compositeAxisFilterResultSet;
	}

	/**
	 * Query for composite axis filter result set Returned is an array or chars, each index representing a year. If year meets the current
	 * filters, it is assigned '1' otherwise it is assigned '0'.
	 * 
	 */
	private int[] queryCompositeAxisFilterResults() {

		if (this.compositeAxisFilterResultSet == null)
		{
			// Establish new Result Set
			// Display Status Window
			// System.err.println("open progress window");
			// this.fhPlotCommon.setprogressWindowTitle("Please wait.");
			// this.fhPlotCommon.setprogressWindowSubject("Progress;");
			if (silentMode == false)
			{
				this.fhPlotCommon.openProgressWindow(fhPlotCommon.getfhxPlotWin());
			}
			// statusWindow = new FHXPlotStatusWin(fhPlotCommon.getfhxPlotWin(),
			// fhPlotCommon,
			// "Please wait. Composite Axis Filters are being applied to your plot ...",
			// "Progress:");
			this.compositeAxisFilterResultSet = this.computeFilterResultSet();
			// System.err.println("about to close progress window");

			if (silentMode == false)
			{
				this.fhPlotCommon.closeProgressWindow();
			}
		}
		else
		{
			// Result Set exists, do nothing.
		}

		return this.compositeAxisFilterResultSet;
	}

	private int[] computeFilterResultSet() {

		int percentScarred;
		float percentScarredFloat;
		int sampleDepth;
		int countScarred;

		if (this.compositeAxisFilterResultSet == null)
		{
			// Establish new Result Set
			int minimumPercentScarred = fhPlotCommon.getfhxPlotOptionsManager().getcompositeAxisFilterOptions().getminimumPercentScarred();
			int minimumSampleDepth = fhPlotCommon.getfhxPlotOptionsManager().getcompositeAxisFilterOptions().getminimumSampleDepth();
			int minimumSamples = fhPlotCommon.getfhxPlotOptionsManager().getcompositeAxisFilterOptions().getminimumNumberSamples();
			boolean includeInjuries = fhPlotCommon.getfhxPlotOptionsManager().getcompositeAxisFilterOptions().getincludeOtherInjuries();

			log.debug("***************************");
			log.debug("    minimumPercentScarred<" + minimumPercentScarred + ">");
			log.debug("    minimumSampleDepth<" + minimumSampleDepth + ">");
			log.debug("    minimumCountScarred<" + minimumSamples + ">");
			log.debug("    includeOtherInjuries<" + includeInjuries + ">");
			log.debug("***************************");

			this.compositeAxisFilterResultSet = new int[this.numYears];
			this.sampleDepthResultSet = new int[this.numYears];
			this.percentScarredResultSet = new float[this.numYears];

			for (int i = 0, year = this.startYear; i < this.numYears; i++, year++)
			{
				if ((year % 25) == 0)
				{
					// Append to Status Window
					fhPlotCommon.setprogressWindowMessage(new String("Processing year <" + year + "> of <" + endYear + "> ..."));
				}

				// Assume year meets filters until proved otherwise
				countScarred = this.countScarred(year, false);
				int numerator;
				if (includeInjuries == false)
				{
					numerator = countScarred;
				}
				else
				{ // Include other injuries is true
					numerator = this.countScarred(year, true);
				}
				sampleDepth = this.sampleDepth(year);
				this.sampleDepthResultSet[i] = sampleDepth;
				percentScarred = 0;
				percentScarredFloat = 0;
				if (sampleDepth > 0)
				{
					percentScarred = (int) ((numerator * 100.0) / sampleDepth);
					percentScarredFloat = (float) ((numerator * 100.0) / sampleDepth);
				}
				this.percentScarredResultSet[i] = percentScarredFloat;
				// log.debug("year<" + year + ">countScarred<" + countScarred + ">sampleDepth<" + sampleDepth + ">percentScarredFloat<" +
// percentScarredFloat + ">\n");
				if ((percentScarred >= minimumPercentScarred) && (sampleDepth >= minimumSampleDepth) && (numerator >= minimumSamples))
				{
					this.compositeAxisFilterResultSet[i] = percentScarred;
				}
				else
				{
					this.compositeAxisFilterResultSet[i] = 0;
					continue;
				}
			}

			// Append to Status Window
			fhPlotCommon.setprogressWindowMessage(new String("Finished Processing year <" + endYear + "> of <" + endYear + ">"));
		}
		else
		{
			// Result Set for query already exists, do nothing.
		}

		return this.compositeAxisFilterResultSet;
	}

	/**
	 * Return years that meet the composite axis filters in Epoch format
	 * 
	 */
	public String[] compositeAxisFilterQueryResultsInEpochFormat() {

		String[] rval = null;
		int[] queryResults;
		int countOfYearsMeetQuery;
		int index;
		int year;
		queryResults = queryCompositeAxisFilterResults();

		// Get the number of years that meet the filter
		countOfYearsMeetQuery = 0;
		for (int i = 0; i < queryResults.length; i++)
		{
			if (queryResults[i] > 0)
			{
				countOfYearsMeetQuery++;
			}
		}

		rval = new String[countOfYearsMeetQuery + 1];
		rval[0] = fhPlotCommon.getfhxPlotOptionsManager().gettitle();
		index = 1;
		year = this.startYear;
		for (int i = 0; i < queryResults.length; i++)
		{
			if (queryResults[i] > 0)
			{
				rval[index] = String.valueOf(year);
				index++;
			}
			year++;
		}
		return rval;
	}

	public int countOfYearsMeetFilters() {

		int countOfYearsMeetQuery = 0;
		int[] queryResults;

		queryResults = queryCompositeAxisFilterResults();

		// Get the number of years that meet the filter
		countOfYearsMeetQuery = 0;
		for (int i = 0; i < queryResults.length; i++)
		{
			if (queryResults[i] > 0)
			{
				countOfYearsMeetQuery++;
			}
		}

		return countOfYearsMeetQuery;
	}

	/**
	 * Return FHX2 format thats meet the composite axis filters.
	 * 
	 */

	public String[] compositeAxisFilterResultsInFHX2Format() {

		String[] rval = null;
		int[] queryResults;
		// int countOfYearsMeetQuery;
		int index;
		int year;

		queryResults = queryCompositeAxisFilterResults();
		// countOfYearsMeetQuery = countOfYearsMeetFilters();

		rval = new String[numYears + 3];
		rval[0] = "FHX2 FORMAT";
		rval[1] = String.valueOf(this.startYear);
		rval[1] = new String(rval[1] + "1 0");
		rval[2] = new String(" ");
		rval[3] = new String("{   " + this.startYear);
		index = 4;

		year = this.startYear + 1;

		int qrIndex = 1;
		int countScarred = 0;
		for (int i = index; i < rval.length; i++)
		{
			if (queryResults[qrIndex] == '0')
			{
				rval[i] = new String(".   " + year);
			}
			else if (queryResults[qrIndex] == '1')
			{
				rval[i] = new String(".   " + year);
				countScarred = this.countScarred(year, false);
				if (countScarred > 0)
				{
					rval[i] = new String("U   " + year);
				}
			}
			year++;
			qrIndex++;
		}

		return rval;
	}

	public int[] FHX2ColumnNosBeingPlotted() {

		return fhPlotCommon.getfhxPlotOptionsManager().getseriesPlottedOptions().getseriesPlottedFHX2ColumnNo();
	}

	public int countOfFHX2ColumnNosBeingPlotted() {

		int rval = 0;
		int[] columnNosBeingPlotted = FHX2ColumnNosBeingPlotted();
		rval = columnNosBeingPlotted.length;
		return rval;
	}

	public String[] FHX2SeriesNamesOfColumnsBeingPlotted() {

		String[] rval;
		int[] columnNosBeingPlotted = FHX2ColumnNosBeingPlotted();
		rval = new String[columnNosBeingPlotted.length];
		for (int i = 0; i < columnNosBeingPlotted.length; i++)
		{
			rval[i] = this.sampleNames[columnNosBeingPlotted[i] - 1];
		}
		return rval;
	}

	public int[] FHX2EarliestRecorderYearOfSeriesBeingPlotted() {

		int[] rval;
		int[] columnNosBeingPlotted = FHX2ColumnNosBeingPlotted();
		rval = new int[columnNosBeingPlotted.length];
		for (int i = 0; i < columnNosBeingPlotted.length; i++)
		{
			rval[i] = this.sampleStartYear[columnNosBeingPlotted[i] - 1];
		}
		return rval;
	}

	public int[] FHX2MostRecentRecorderYearOfSeriesBeingPlotted() {

		int[] rval;
		int[] columnNosBeingPlotted = FHX2ColumnNosBeingPlotted();
		rval = new int[columnNosBeingPlotted.length];
		for (int i = 0; i < columnNosBeingPlotted.length; i++)
		{
			rval[i] = this.sampleEndYear[columnNosBeingPlotted[i] - 1];
		}
		return rval;
	}

	public boolean isFHX2ColumnBeingPlotted(int columnNoIn) {

		boolean rval = false;
		int[] colNosBeingPlotted = FHX2ColumnNosBeingPlotted();
		for (int i = 0; i < colNosBeingPlotted.length; i++)
		{
			if (colNosBeingPlotted[i] == columnNoIn)
			{
				rval = true;
				break;
			}
		}
		return rval;
	}

	/************** Finish of Methods used for Filtering and Analysis ****************/

	/****************************************/
	// JFreeChart Series Creation Methods.
	// Methods that create JFreeChart Series based in the FHX data.
	/****************************************/
	/**
	 * Creates a series of symbols for a given fire, injury, or selected lifecycle events: D E M L A U d e m l a u [ ] { }
	 * 
	 * @param eventChar character that defines the event.
	 * @param sampleId identifier of the sample for which the series is to be generated.
	 */
	public XYSeries createSymbolSeries(char eventChar, int sampleId) {

		XYSeries rtnSeries = null;
		char[] eventCharArray = null;
		eventCharArray = new char[1];
		eventCharArray[0] = eventChar;
		rtnSeries = new XYSeries(new String(eventCharArray));
		// Code for alpha release - remove if condition for beta release
		if (eventChar == 'D' || eventChar == 'E' || eventChar == 'M' || eventChar == 'L' || eventChar == 'A' || eventChar == 'U'
				|| eventChar == 'd' || eventChar == 'e' || eventChar == 'm' || eventChar == 'l' || eventChar == 'a' || eventChar == 'u'
				|| eventChar == '[' || eventChar == ']' || eventChar == '{' || eventChar == '}')
		{

			for (int i = 0; i < numYears; i++)
			{
				if (this.sampleDominatedMatrix[sampleId][i] == eventChar)
				{
					rtnSeries.add(this.startYear + i, 0);
					/*
					 * if (sampleId == 19){ log.debug("In createSymbolSeries, i <" + i + ">"); log.debug("startYear<" + startYear + ">");
					 * int addstart = this.startYear + i; log.debug("add at year<" + addstart + ">"); log.debug("eventChar<" + eventChar +
					 * ">\n"); }
					 */

				}
			}
		}

		// Debug code
		if (sampleId == 0)
		{
			// log.debug("createSymbolSeries eventChar<" + eventChar + ">sampleId<" + sampleId + ">xyseriesCount<" +
// rtnSeries.getItemCount() + ">\n");
		}
		return rtnSeries;
	}

	/**
	 * Creates a series of dotted-line segments for specific lifecycle event: . nonrecorder years
	 * 
	 * @param eventChar character that defines the event.
	 * @param sampleId identifier of the sample for which the series is to be generated.
	 */
	public XYSeries createDottedLineSeries(char eventChar, int sampleId) {

		XYSeries rtnSeries = null;
		char[] eventCharArray = null;
		eventCharArray = new char[1];
		eventCharArray[0] = eventChar;
		rtnSeries = new XYSeries(new String(eventCharArray));

		for (int i = 0; i < numYears; i++)
		{
			if (this.sampleDominatedMatrix[sampleId][i] == eventChar)
			{
				rtnSeries.add((this.startYear) - 1 + i, 0);
				rtnSeries.add((this.startYear) + i, 0);
				rtnSeries.add((this.startYear) + 1 + i, 0);
			}
		}

		return rtnSeries;
	}

	/**
	 * Creates a series of solid-line segments for specific lifecycle event: | recorder years
	 * 
	 * @param eventChar character that defines the event.
	 * @param sampleId identifier of the sample for which the series is to be generated.
	 */
	public XYSeries createSolidLineSeries(char eventChar, int sampleId) {

		XYSeries rtnSeries = null;
		char[] eventCharArray = null;
		eventCharArray = new char[1];
		eventCharArray[0] = eventChar;
		rtnSeries = new XYSeries(new String(eventCharArray));

		for (int i = 0; i < numYears; i++)
		{
			if (this.sampleDominatedMatrix[sampleId][i] == eventChar)
			{
				rtnSeries.add((this.startYear) - 1 + i, 0);
				rtnSeries.add((this.startYear) + i, 0);
				rtnSeries.add((this.startYear) + 1 + i, 0);
			}
		}

		return rtnSeries;
	}

	/**
	 * Creates series of lines for Composite Axis
	 */
	public XYSeries createCompositeAxisLineSeries() {

		// log.debug("Enter FHXPlotDataManager::createCompositeAxisLineSeries\n");
		// log.debug("initialDomaimnRange.lowerbound<" + initialDomainRange.getLowerBound() + ">\n");
		// log.debug("initialDomaimnRange.upperbound<" + initialDomainRange.getUpperBound() + ">\n");

		XYSeries rtnSeries = null;
		int[] queryResults;
		rtnSeries = new XYSeries(new String(""));
		queryResults = this.queryCompositeAxisFilterResults();

		int currentYear = this.startYear;
		for (int i = 0; i < numYears; i++, currentYear++)
		{
			if (queryResults[i] > 0)
			{
				rtnSeries.add(currentYear, 0);
				rtnSeries.add(currentYear, 100);
				rtnSeries.add(currentYear, null);
			}
		}

		// log.debug("Exit FHXPlotDataManager::createCompositeAxisLineSeries\n");

		return rtnSeries;
	}

	/**
	 * Creates lines for Sample Depth Fire Index plot
	 */
	public IntervalXYDataset createSampleDepthLineSeries() {

		XYSeries rtnSeries = null;
		int[] queryResults;
		this.queryCompositeAxisFilterResults(); // Make sure that this.querySampleDepthResultSet is up-to-date
		queryResults = this.sampleDepthResultSet;
		rtnSeries = new XYSeries(new String(""));

		// log.debug("Sample Depth Index Plot values:\n");

		int currentYear = this.startYear;
		for (int i = 0; i < numYears; i++, currentYear++)
		{
			rtnSeries.add(currentYear, queryResults[i]);
			// log.debugcurrentYear + " " + queryResults[i] + "\n");
		}

		// log.debug("Sample Depth Index Plot values - are complete\n\n");

		XYSeriesCollection dataset = new XYSeriesCollection(rtnSeries);
		return dataset;
	}

	public IntervalXYDataset createPercentScarredXYDataset() {

		float[] yValues;

		this.queryCompositeAxisFilterResults(); // Make sure that this.queryPercentScarredResultSet is up-to-date
		yValues = this.percentScarredResultSet;
		return new PercentScarredXYDataset(this.startYear, this.numYears, yValues, fhPlotCommon);
	}

	/**
	 * Creates lines for percent scarred Fire Index plot
	 */
	/*
	 * public XYSeries createPercentScarredLineSeries() { //log.debug("Enter FHXPlotDataManager::createPercentScarredLineSeries\n");
	 * XYSeries rtnSeries = null; float[] queryResults; this.queryCompositeAxisFilterResults(); // Make sure that
	 * this.queryPercentScarredResultSet is up-to-date queryResults = this.percentScarredResultSet; rtnSeries = new XYSeries(new
	 * String(""));
	 * 
	 * int currentYear = this.startYear; for (int i = 0; i < numYears; i++, currentYear++) { rtnSeries.add(currentYear, 0);
	 * rtnSeries.add(currentYear, queryResults[i]); rtnSeries.add(currentYear, null); }
	 * 
	 * return rtnSeries; }
	 */

	/************* Finish of Create JFreeChart series methods ***************/

	/****************************************/
	// Get and Set Methods for private members
	/****************************************/
	public int getOldestRecordingYear(int sampleId) {

		return sampleStartYear[sampleId];
	}

	public int getMostRecentRecordingYear(int sampleId) {

		return sampleEndYear[sampleId];
	}

	public int getnumSamples() {

		return numSamples;
	}

	public String getsampleName(int sampleId) {

		return sampleNames[sampleId];
	}

	public int getstartYear() {

		return startYear;
	}

	public int getendYear() {

		return endYear;
	}

	public int getnumYears() {

		return numYears;
	}

	public String[] getFHX2InputFileContent() {

		String[] rval;
		rval = new String[filearray.size()];
		for (int i = 0; i < filearray.size(); i++)
		{
			rval[i] = new String((String) filearray.get(i));
		}
		return rval;
	}

	public boolean getsilentMode() {

		return this.silentMode;
	}

	/************* Finish of Get and Set methods for private members *******************/

	/****************************************/
	// Debug method that dumps the major data structures of this class.
	// This method is for use in development and debugging only.
	/****************************************/
	@SuppressWarnings("unused")
	private void dumpDataStructures() {

		log.debug("\n**************************************************\n");
		log.debug("\nDUMP DATA MANAGEMENTT STRUCTURES\n");
		log.debug("\n**************************************************\n");
		// Output FHX File content
		log.debug("\n**************************************************\n");
		log.debug("\nOutput FHX File content\n");
		log.debug("\n**************************************************\n");
		for (int i = 0; i < filearray.size(); i++)
			log.debug(filearray.get(i) + "\n");
		// Output Sample Names
		log.debug("\n**************************************************\n");
		log.debug("\nOutput Sample Names\n");
		log.debug("\n**************************************************\n");
		for (int i = 0; i < numSamples; i++)
		{
			log.debug("sampleNames[" + i + "]<" + sampleNames[i] + ">\n");
		}
		// Output FHX year data
		log.debug("\n**************************************************\n");
		log.debug("\nOutput FHX Year data\n");
		log.debug("\n**************************************************\n");
		int index = sentinalStringIndex + sampleNameSize + 2;
		int count = index;
		for (int i = startYear; i <= endYear; i++)
		{
			log.debug("year-row<" + (String) filearray.get(count) + ">\n");
			count++;
		}
		// output sample dominated matrix
		log.debug("\n**************************************************\n");
		log.debug("\nOutput Sample Dominated Matrix\n");
		log.debug("\n**************************************************\n");
		for (int i = 0; i < numSamples; i++)
		{
			String tmp = new String(sampleDominatedMatrix[i]);
			log.debug("NEXTSAMPLE<" + tmp + ">\n");
		}
		// output year dominated matrix
		log.debug("\n**************************************************\n");
		log.debug("\nOutput year Dominated Matrix\n");
		log.debug("\n**************************************************\n");
		for (int i = 0; i < numYears; i++)
		{
			String tmp = new String(yearDominatedMatrix[i]);
			log.debug("NEXTYEAR<" + tmp + ">\n");
		}
		// output start recorder year, and end recorder year
		log.debug("\n**************************************************\n");
		log.debug("\nOutput start recorder year, and end recorder year\n");
		log.debug("\n**************************************************\n");

		for (int i = 0; i < this.numSamples; i++)
		{
			log.debug("sampleId<" + i + ">startYear<" + sampleStartYear[i] + ">endYear<" + sampleEndYear[i] + ">\n");
		}
	}
}
