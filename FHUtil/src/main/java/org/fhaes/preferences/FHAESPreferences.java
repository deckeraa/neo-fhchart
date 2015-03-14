/*******************************************************************************
 * Copyright (C) 2011 Peter Brewer.
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
 *     Peter Brewer
 ******************************************************************************/
package org.fhaes.preferences;

/*******************************************************************************
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
 *     Peter Brewer
 ******************************************************************************/

import java.awt.Color;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.prefs.Preferences;

import org.fhaes.enums.AnalysisLabelType;
import org.fhaes.enums.AnalysisType;
import org.fhaes.enums.FireFilterType;
import org.fhaes.enums.EventTypeToProcess;
import org.fhaes.enums.NoDataLabel;
import org.fhaes.enums.ResamplingType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * <p>
 * The FHAESPreference system enables us to save and retrieve values across sessions. The values are stored in locations specific
 * to the host operating system. For instance in Windows they are stored in the registry and in Linux they are stored in a system
 * file in the users home.
 * </p>
 * 
 * <p>
 * Preferences should be accessed and set through a static variable e.g. App.prefs. There are a variety of setter functions
 * depending on the data type trying to be stored e.g. setIntPref() setDoublePref() but they all follow the same style taking a
 * PrefKey and a value. The PrefKey is an enumeration listed in this class containing the key names that are stored in the users
 * preferences file/registry.
 * </p>
 * 
 * <p>
 * Similarly there are accessor methods depending on the data type being extracted e.g. getIntPref() getDoublePref(). These all
 * require a PrefKey and a default value to be returned when no value is found.
 * </p>
 * 
 * 
 * @author pwb48
 * 
 */
public class FHAESPreferences {
	private static String ARRAY_DELIMITER = ";";

	private static Preferences prefs = Preferences
			.userNodeForPackage(FHAESPreferences.class);
	private static final Logger log = LoggerFactory
			.getLogger(FHAESPreferences.class);
	private Boolean silentMode = false;
	private ArrayList<PrefsListener> listeners = new ArrayList<PrefsListener>();

	/**
	 * This enum contains the keys for the preference values stored and used by FHAES
	 * 
	 * @author pwb48
	 * 
	 */
	public enum PrefKey {

		LOCALE_LANGUAGE_CODE("locale.language"), LOCALE_COUNTRY_CODE(
				"locale.country"), PREF_LAST_READ_FOLDER("LastFolderVisited"), PREF_LAST_READ_TIME_SERIES_FOLDER(
				"TimeSeriesFolderVisited"), PREF_LAST_READ_EVENT_LIST_FOLDER(
				"EventListFolderVisited"), PREF_LAST_EXPORT_FOLDER(
				"LastExportFolder"), RECENT_DOCUMENT_COUNT(
				"RecentDocumentCount"), PREF_LAST_EXPORT_FORMAT(
				"LastExportFormat"),

		RECENT_DOCUMENT_LIST("RecentDocumentList"), DONT_CHECK_FOR_UPDATES(
				"noUpdates"), UPDATES_LATE_CHECKED("updatesLastChecked"),

		SEASONALITY_FIRST_GROUP_DORMANT("seasonalityFirstGroupDormant"), SEASONALITY_FIRST_GROUP_EARLY_EARLY(
				"seasonalityFirstGroupEarlyEarly"), SEASONALITY_FIRST_GROUP_MIDDLE_EARLY(
				"seasonalityFirstGroupMiddleEarly"), SEASONALITY_FIRST_GROUP_LATE_EARLY(
				"seasonalityFirstGroupLateEarly"), SEASONALITY_FIRST_GROUP_LATE(
				"seasonalityFirstGroupLate"), SEASONALITY_SECOND_GROUP_DORMANT(
				"seasonalitySecondGroupDormant"), SEASONALITY_SECOND_GROUP_EARLY_EARLY(
				"seasonalitySecondGroupEarlyEarly"), SEASONALITY_SECOND_GROUP_MIDDLE_EARLY(
				"seasonalitySecondGroupMiddleEarly"), SEASONALITY_SECOND_GROUP_LATE_EARLY(
				"seasonalitySecondGroupLateEarly"), SEASONALITY_SECOND_GROUP_LATE(
				"seasonalitySecondGroupLate"),

		JSEA_CONTINUOUS_TIME_SERIES_FILE("continuousTimeSeriesFile"), JSEA_EVENT_LIST_FILE(
				"eventListFile"), JSEA_CHART_TITLE("jseaChartTitle"), JSEA_YAXIS_LABEL(
				"jseaYAxisLabel"), JSEA_LAGS_PRIOR_TO_EVENT(
				"jseaLagsPriorToEvent"), JSEA_LAGS_AFTER_EVENT(
				"jseaLagsAfterEvent"), JSEA_INCLUDE_INCOMPLETE_WINDOW(
				"jseaIncludeIncompleteWindow"), JSEA_SIMULATION_COUNT(
				"jseaSimulationCount"), JSEA_SEED_NUMBER("jseaSeedNumber"), JSEA_P_VALUE(
				"jseaPValue"), JSEA_FIRST_YEAR("jseaFirstYear"), JSEA_LAST_YEAR(
				"jseaLastYear"),

		SHOW_FILE_SAVED_MESSAGE("showFileSavedMessage"),

		SSIZ_SIMULATION_COUNT("ssizSimulationCount"), SSIZ_SEED_NUMBER(
				"ssizSeedNumber"), SSIZ_ALL_YEARS("ssizProcessAllYears"), SSIZ_RESAMPLING_TYPE(
				"ssizResamplingType"), SSIZ_CHK_COMMON_YEARS(
				"ssizChkCommonYears"), SSIZ_CHK_EXCLUDE_SERIES_WITH_NO_EVENTS(
				"ssizExcludeSeriesWithNoEvents"),

		MATRIX_NO_DATA_LABEL("matrixNoDataLabel"),

		COMPOSITE_FILTER_TYPE("compositeFilterType"), COMPOSITE_FILTER_VALUE(
				"compositeFilterValue"), COMPOSITE_DISTANCE_THRESHOLD_KM(
				"compositeDistanceThreshold"), COMPOSITE_MIN_SAMPLES(
				"compositeMinimumSamples"),

		INTERVALS_ANALYSIS_TYPE("intervalsAnalysisType"), INTERVALS_INCLUDE_OTHER_INJURIES(
				"intervalsIncludeOtherInjuries"), INTERVALS_ALPHA_LEVEL(
				"intervalsAlphaLevel"),

		EVENT_TYPE_TO_PROCESS("matrixEventType"),

		RANGE_CALC_OVER_ALL_YEARS("allYears"), RANGE_FIRST_YEAR("firstYear"), RANGE_LAST_YEAR(
				"lastYear"), RANGE_OVERLAP_REQUIRED("overlapRequired"),

		ANALYSIS_LABEL_TYPE("AnalysisLabelType"),

		LARGE_DATASET_WARNING_DISABLED("largeDatasetWarningDisabled"), SHAPEFILE_OUTPUT_STYLE(
				"shapefileOutputStyle"),

		ENFORCE_FHX2_RESTRICTIONS("enforceFHX2Restrictions"), COLORBAR_GROUPNAME_1(
				"colorbarGroupName1"), COLORBAR_GROUPNAME_2(
				"colorbarGroupName2"), COLORBAR_GROUPNAME_3(
				"colorbarGroupName3"), COLORBAR_GROUPNAME_4(
				"colorbarGroupName4"), COLORBAR_GROUPNAME_5(
				"colorbarGroupName5"), COLORBAR_GROUPNAME_6(
				"colorbarGroupName6"), COLORBAR_COLOR_1("colorbarColor1"), COLORBAR_COLOR_2(
				"colorbarColor2"), COLORBAR_COLOR_3("colorbarColor3"), COLORBAR_COLOR_4(
				"colorbarColor4"), COLORBAR_COLOR_5("colorbarColor5"), COLORBAR_COLOR_6(
				"colorbarColor6"), COLORBAR_ITEMS_GROUP0("colorbarItemsGroup0"), COLORBAR_ITEMS_GROUP1(
				"colorbarItemsGroup1"), COLORBAR_ITEMS_GROUP2(
				"colorbarItemsGroup2"), COLORBAR_ITEMS_GROUP3(
				"colorbarItemsGroup3"), COLORBAR_ITEMS_GROUP4(
				"colorbarItemsGroup4"), COLORBAR_ITEMS_GROUP5(
				"colorbarItemsGroup5"), COLORBAR_ITEMS_GROUP6(
				"colorbarItemsGroup6"),

		SCREEN_BOUNDS_X("screenBoundsX"), SCREEN_BOUNDS_Y("screenBoundsY"), SCREEN_WIDTH(
				"screenWidth"), SCREEN_HEIGHT("screenHeight"), DONT_REQUEST_PARAM_CONFIRMATION(
				"dontRequestParamConfirmation"), DONT_SHOW_QUICK_LAUNCH(
				"dontShowQuickLaunch"),

		;

		private String key;

		private PrefKey(String key) {
			this.key = key;
		}

		public String getValue() {
			return key;
		}
	}

	/**
	 * Sets the silentMode on and off. see isSilentMode
	 * 
	 * @param mode
	 */
	public void setSilentMode(Boolean mode) {
		this.silentMode = mode;

	}

	/**
	 * Whether the silentMode is on. When silentMode is on, PrefsListeners are not notified of a change. This is useful if you are
	 * intending to save multiple preference values and don't want them each to fire off separately. You *MUST* ensure that you
	 * turn the silent mode off again once you are done!
	 * 
	 * @return
	 */
	public boolean isSilentMode() {
		return this.silentMode;
	}

	/**
	 * Add a listener for changes to preferences
	 * 
	 * @param l
	 */
	public void addPrefsListener(PrefsListener l) {

		log.debug("PrefsListener added");

		listeners.add(l);
	}

	/**
	 * Remove a listener when you no longer what to hear about changes to preferences
	 * 
	 * @param l
	 */
	public void removePrefsListener(PrefsListener l) {
		listeners.remove(l);
	}

	/**
	 * Fire a preference change event for a specific PrefKey
	 * 
	 * @param pref
	 */
	public void firePrefChanged(PrefKey pref) {

		if (this.silentMode)
			return;

		log.debug("Preference change fired");

		PrefsEvent e = new PrefsEvent(FHAESPreferences.class, pref);

		for (PrefsListener l : listeners) {
			l.prefChanged(e);
		}

	}

	/**
	 * Method for getting the string value of a preference
	 * 
	 * @param key
	 * @param deflt
	 * @return
	 */
	public String getPref(PrefKey key, String deflt) {
		return prefs.get(key.getValue(), deflt);
	}

	public ArrayList<String> getArrayListPref(PrefKey key,
			ArrayList<String> deflt) {
		String value = prefs.get(key.getValue(), null);
		if (value == null) {
			return deflt;
		}

		String[] values = value.split(ARRAY_DELIMITER);

		ArrayList<String> arrlist = new ArrayList<String>();

		for (String v : values) {
			arrlist.add(v);
		}

		if (values.length > 0) {
			return arrlist;
		}

		return deflt;

	}

	/**
	 * Get the value of an AnalysisType preference
	 * 
	 * @param key
	 * @param deflt
	 * @return
	 */
	public AnalysisType getAnalysisTypePref(PrefKey key, AnalysisType deflt) {
		String value = "";
		if (deflt == null) {
			value = prefs.get(key.getValue(), null);
		} else {
			value = prefs.get(key.getValue(), deflt.name());
		}

		if (value == null)
			return deflt;
		if (AnalysisType.fromName(value) == null)
			return deflt;
		return AnalysisType.fromName(value);
	}

	/**
	 * Set the value of a preference
	 * 
	 * @param key
	 * @param value
	 */
	public void setArrayListPref(PrefKey key, ArrayList<String> values) {
		String pref = key.getValue();
		String arrAsStr = "";

		// support removing via set(null)
		if (values == null) {
			prefs.remove(pref);
		} else {

			for (String value : values) {
				arrAsStr += value + ARRAY_DELIMITER;
			}
			setPref(key, arrAsStr);
		}
	}

	/**
	 * Set the value of a preference to the specified AnalysisType
	 * 
	 * @param key
	 * @param value
	 */
	public void setAnalysisTypePref(PrefKey key, AnalysisType value) {
		setPref(key, value.toString());
	}

	/**
	 * Get the value of an FireFilterType preference
	 * 
	 * @param key
	 * @param deflt
	 * @return
	 */
	public FireFilterType getFireFilterTypePref(PrefKey key,
			FireFilterType deflt) {
		String value = "";
		if (deflt == null) {
			value = prefs.get(key.getValue(), null);
		} else {
			value = prefs.get(key.getValue(), deflt.name());
		}
		if (value == null)
			return deflt;
		if (FireFilterType.fromName(value) == null)
			return deflt;
		return FireFilterType.fromName(value);
	}

	/**
	 * Get the value of a EventTypeToProcess preference
	 * 
	 * @param key
	 * @param deflt
	 * @return
	 */
	public EventTypeToProcess getEventTypePref(PrefKey key,
			EventTypeToProcess deflt) {
		String value = "";
		if (deflt == null) {
			value = prefs.get(key.getValue(), null);
		} else {
			value = prefs.get(key.getValue(), deflt.name());
		}
		if (value == null)
			return deflt;
		if (EventTypeToProcess.fromName(value) == null)
			return deflt;
		return EventTypeToProcess.fromName(value);
	}

	public NoDataLabel getNoDataLabelPref(PrefKey key, NoDataLabel deflt) {
		String value = "";
		if (deflt == null) {
			value = prefs.get(key.getValue(), null);
		} else {
			value = prefs.get(key.getValue(), deflt.name());
		}

		if (value == null)
			return NoDataLabel.NULL;

		return NoDataLabel.fromString(value);
	}

	/**
	 * Get the value of a AnalysisLabelType preference
	 * 
	 * @param key
	 * @param deflt
	 * @return
	 */
	public AnalysisLabelType getAnalysisLabelTypePref(PrefKey key,
			AnalysisLabelType deflt) {
		String value = "";
		if (deflt == null) {
			value = prefs.get(key.getValue(), null);
		} else {
			value = prefs.get(key.getValue(), deflt.name());
		}
		if (value == null)
			return deflt;
		return AnalysisLabelType.fromName(value);
	}

	/**
	 * Get the value of a AnalysisLabelType preference
	 * 
	 * @param key
	 * @param deflt
	 * @return
	 */
	public ResamplingType getResamplingTypePref(PrefKey key,
			ResamplingType deflt) {
		String value = "";
		if (deflt == null) {
			value = prefs.get(key.getValue(), null);
		} else {
			value = prefs.get(key.getValue(), deflt.name());
		}
		if (value == null)
			return deflt;
		return ResamplingType.fromName(value);
	}

	/**
	 * Set the value of a preference to the specified color
	 * 
	 * @param pref
	 * @param value
	 */
	public void setColorPref(PrefKey pref, Color value) {
		String encoded = "#" + Integer.toHexString(value.getRGB() & 0x00ffffff);
		setPref(pref, encoded);
	}

	/**
	 * Set the value of a preference to the specified AnalysisLabelType
	 * 
	 * @param key
	 * @param value
	 */
	public void setAnalysisLabelTypePref(PrefKey key, AnalysisLabelType value) {
		setPref(key, value.toString());
	}

	/**
	 * Set the value of a preference to the specified ResamplingType
	 * 
	 * @param key
	 * @param value
	 */
	public void setResamplingTypePref(PrefKey key, ResamplingType value) {
		setPref(key, value.toString());
	}

	/**
	 * Set the value of a preference to the specified FireFilterType
	 * 
	 * @param key
	 * @param value
	 */
	public void setFireFilterTypePref(PrefKey key, FireFilterType value) {
		setPref(key, value.toString());
	}

	/**
	 * Set the value of a preference to the specified EventTypeToProcess
	 * 
	 * @param key
	 * @param value
	 */
	public void setEventTypePref(PrefKey key, EventTypeToProcess value) {
		setPref(key, value.toString());
	}

	public void setNoDataLabelPref(PrefKey key, NoDataLabel value) {
		setPref(key, value.toString());
	}

	/**
	 * Store a string preference for a given key
	 * 
	 * @param key
	 * @param value
	 */
	public void setPref(PrefKey key, String value) {
		prefs.put(key.getValue(), value);
		firePrefChanged(key);
	}

	/**
	 * Store a string ArrayList preference for a given key
	 * 
	 * @param key
	 * @param arr
	 */
	public void setStringArrayPref(PrefKey key, List<String> arr) {
		if (arr == null) {
			prefs.put(key.getValue(), "");
		} else {
			String s = arr.toString();
			prefs.put(key.getValue(), s);
		}
		firePrefChanged(key);
	}

	/**
	 * Add a string to a preference array. If string is present then remove it first then add at the top of the list
	 * 
	 * @param key
	 * @param str
	 * @param maxSize
	 */
	public void addStringtoPrefArray(PrefKey key, String str, Integer maxSize) {
		str = str.replace(",", "|||");

		LinkedList<String> arr = getStringArrayPref(key);

		if (arr == null) {
			arr = new LinkedList<String>();
		} else {
			// Remove it first if it is already there
			removeStringFromPrefArray(key, str);
			arr = getStringArrayPref(key);
			// Add new item to top of list
			arr.push(str);
		}

		// Remove items when there are too many in list
		while (arr.size() > maxSize) {
			arr.pollLast();
		}

		// Save array to preferences
		setStringArrayPref(key, arr);
	}

	/**
	 * Remove a string from a preference array
	 * 
	 * @param key
	 * @param str
	 */
	public void removeStringFromPrefArray(PrefKey key, String str) {
		LinkedList<String> arr = getStringArrayPref(key);

		if (arr == null)
			return;

		int indexFound = -1;
		int i = 0;

		for (String item : arr) {

			if (item.equals(str)) {
				indexFound = i;
				break;
			}
			i++;
		}

		if (indexFound != -1) {
			arr.remove(indexFound);
			setStringArrayPref(key, arr);
		}
	}

	/**
	 * Get a preference as a ArrayList<String>()
	 * 
	 * @param key
	 * @return
	 */
	public LinkedList<String> getStringArrayPref(PrefKey key) {
		String raw = prefs.get(key.getValue(), null);
		// log.debug("Raw string array as string is : "+raw);

		if (raw == null)
			return null;
		if (!raw.startsWith("[") || !raw.endsWith("]")) {
			return null;
		}

		raw = raw.substring(1, raw.length() - 1);

		LinkedList<String> arr = new LinkedList<String>();

		String[] split = raw.split(", ");

		// log.debug("Pref array contains "+split.length+" items");

		if (split.length < 1)
			return null;

		for (String s : split) {
			// Trim off brackets and save
			arr.add(s.replace("|||", ","));
		}

		return arr;
	}

	/**
	 * Get a color for the specified preference key
	 * 
	 * @param key
	 * @param deflt
	 * @return
	 */
	public Color getColorPref(PrefKey key, Color deflt) {
		String value = prefs.get(key.getValue(), null);
		if (value == null)
			return deflt;
		try {
			return Color.decode(value);
		} catch (NumberFormatException nfe) {
			log.warn("Invalid color for preference '" + key.getValue() + "': "
					+ value);
			return deflt;
		}
	}

	/**
	 * Method for getting the boolean value of a preference
	 * 
	 * @param key
	 * @param deflt
	 * @return
	 */
	public Boolean getBooleanPref(PrefKey key, Boolean deflt) {
		String value = "";
		if (deflt == null) {
			value = prefs.get(key.getValue(), null);
		} else {
			value = prefs.get(key.getValue(), Boolean.toString(deflt));
		}
		if (value == null)
			return deflt;
		return Boolean.parseBoolean(value);
	}

	/**
	 * Set the value of a preference to the specified boolean
	 * 
	 * @param key
	 * @param value
	 */
	public void setBooleanPref(PrefKey key, boolean value) {
		setPref(key, Boolean.toString(value));
	}

	/**
	 * Set the value of a preference to the specified int
	 * 
	 * @param pref
	 * @param value
	 */
	public void setIntPref(PrefKey pref, int value) {
		// log.debug("Setting integer pref value for key "+pref+" to value: "+value);
		setPref(pref, Integer.toString(value));
	}

	/**
	 * Set the value of a preference to the specified Double
	 * 
	 * @param pref
	 * @param value
	 */
	public void setDoublePref(PrefKey pref, Double value) {
		setPref(pref, Double.toString(value));
	}

	/**
	 * Get the specified preference as an int
	 * 
	 * @param key
	 * @param deflt
	 * @return
	 */
	public int getIntPref(PrefKey key, int deflt) {
		// log.debug("Getting integer pref for key: "+key);
		String value = prefs.get(key.getValue(), Integer.toString(deflt));
		// log.debug("Value is = "+value);
		if (value == null)
			return deflt;
		try {
			return Integer.parseInt(value);
		} catch (NumberFormatException nfe) {
			log.warn("Invalid integer for preference '" + key.getValue()
					+ "': " + value);
			return deflt;
		}
	}

	/**
	 * Get the specified preference as an Double
	 * 
	 * @param key
	 * @param deflt
	 * @return
	 */
	public Double getDoublePref(PrefKey key, Double deflt) {
		String value = prefs.get(key.getValue(), Double.toString(deflt));
		if (value == null)
			return deflt;
		try {
			return Double.parseDouble(value);
		} catch (NumberFormatException nfe) {
			log.warn("Invalid double for preference '" + key.getValue() + "': "
					+ value);
			return deflt;
		}
	}

	/**
	 * Remove a preference value
	 * 
	 * @param key
	 */
	public static void removePref(PrefKey key) {
		prefs.remove(key.getValue());
	}

}
