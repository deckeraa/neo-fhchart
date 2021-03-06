package org.fhaes.preferences.wrappers;

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

import java.awt.event.ItemEvent;

import javax.swing.DefaultComboBoxModel;
import javax.swing.JComboBox;

import org.fhaes.enums.FireFilterType;
import org.fhaes.preferences.FHAESPreferences.PrefKey;

@SuppressWarnings("rawtypes")
public class FireFilterTypeWrapper extends ItemWrapper {

	FireFilterType[] formats;

	@SuppressWarnings("unchecked")
	public FireFilterTypeWrapper(JComboBox cbo, PrefKey key,
			Object defaultValue, FireFilterType[] values) {

		super(key, defaultValue, FireFilterType.class);
		initGeneric(cbo, values);
	}

	@SuppressWarnings("unchecked")
	public FireFilterTypeWrapper(JComboBox cbo, PrefKey key, Object defaultValue) {

		super(key, defaultValue, FireFilterType.class);
		initFormats(cbo);
	}

	@SuppressWarnings("unchecked")
	private void initFormats(JComboBox cbo) {

		// show a sample for each format thingy...
		formats = OPTIONS;
		int selectedIdx = -1;

		for (int i = 0; i < OPTIONS.length; i++) {
			if (OPTIONS[i].equals(getValue()))
				selectedIdx = i;
		}

		cbo.setModel(new DefaultComboBoxModel(formats));
		if (selectedIdx >= 0)
			cbo.setSelectedIndex(selectedIdx);

		cbo.addItemListener(this);
	}

	@SuppressWarnings("unchecked")
	private void initGeneric(JComboBox cbo, FireFilterType[] values) {

		// show a sample for each format thingy...
		formats = values;
		int selectedIdx = -1;

		for (int i = 0; i < values.length; i++) {
			if (formats[i].equals(getValue()))
				selectedIdx = i;
		}

		cbo.setModel(new DefaultComboBoxModel(values));
		if (selectedIdx >= 0)
			cbo.setSelectedIndex(selectedIdx);

		cbo.addItemListener(this);
	}

	@SuppressWarnings("unchecked")
	@Override
	public void itemStateChanged(ItemEvent e) {

		int selectedIdx = ((JComboBox) e.getSource()).getSelectedIndex();

		if (selectedIdx >= 0)
			setValue(formats[selectedIdx]);
		else
			setValue(null);
	}

	private final static FireFilterType[] OPTIONS = FireFilterType.values();

}
