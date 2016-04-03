/*
 * Copyright (C) 2011 Nameless Production Committee.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package bebop.util;

import java.util.Locale;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;

import com.ibm.icu.text.Collator;

/**
 * @version 2011/11/16 16:15:47
 */
public final class UIUtility {

    /**
     * <p>
     * Utility method to make the specified table sortable.
     * </p>
     * 
     * @param table A target table.
     */
    public static final void sortable(Table table) {
        if (table != null) {
            for (int i = 0; i < table.getColumnCount(); i++) {
                new TableSorter(table, i);
            }
        }
    }

    /**
     * @version 2011/11/16 16:17:20
     */
    private static class TableSorter implements Listener {

        /** The column index. */
        private final int index;

        /** The target widget. */
        private final Table table;

        /** The sortable column. */
        private final TableColumn column;

        /**
         * @param table
         */
        private TableSorter(Table table, int index) {
            this.index = index;
            this.table = table;
            this.column = table.getColumn(index);
            this.column.addListener(SWT.Selection, this);
        }

        /**
         * @see org.eclipse.swt.widgets.Listener#handleEvent(org.eclipse.swt.widgets.Event)
         */
        @Override
        public void handleEvent(Event e) {
            TableColumn sorted = table.getSortColumn();
            TableColumn selected = (TableColumn) e.widget;

            int direction = table.getSortDirection();

            if (sorted == selected) {
                direction = direction == SWT.UP ? SWT.DOWN : SWT.UP;
            } else {
                table.setSortColumn(selected);
                direction = SWT.UP;
            }

            TableItem[] items = table.getItems();
            Collator collator = Collator.getInstance(Locale.getDefault());

            if (direction == SWT.UP) {
                for (int i = 1; i < items.length; i++) {
                    String value1 = items[i].getText(index);
                    for (int j = 0; j < i; j++) {
                        String value2 = items[j].getText(index);
                        if (collator.compare(value1, value2) < 0) {
                            String[] values = {items[i].getText(0), items[i].getText(1)};
                            items[i].dispose();
                            TableItem item = new TableItem(table, SWT.NONE, j);
                            item.setText(values);
                            items = table.getItems();
                            break;
                        }
                    }
                }
            } else {
                for (int i = 1; i < items.length; i++) {
                    String value1 = items[i].getText(index);
                    for (int j = 0; j < i; j++) {
                        String value2 = items[j].getText(index);
                        if (collator.compare(value1, value2) > 0) {
                            String[] values = {items[i].getText(0), items[i].getText(1)};
                            items[i].dispose();
                            TableItem item = new TableItem(table, SWT.NONE, j);
                            item.setText(values);
                            items = table.getItems();
                            break;
                        }
                    }
                }
            }

            table.setSortColumn(selected);
            table.setSortDirection(direction);
        }
    }

}
