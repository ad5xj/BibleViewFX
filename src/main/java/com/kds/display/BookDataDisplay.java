/*
 * Copyright (C) 2025 org.kds
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
 */
package com.kds.display;

import org.crosswire.jsword.book.Book;
import org.crosswire.jsword.book.BookProvider;
import org.crosswire.jsword.passage.Key;

import java.awt.Component;

import java.beans.PropertyChangeListener;


public interface BookDataDisplay extends BookProvider, PropertyChangeListener {
  public static final String COMPARE_BOOKS = "ComparingBooks";
  
  void clearBookData();
  void setBookData(Book[] paramArrayOfBook, Key paramKey);
  void setCompareBooks(boolean paramBoolean);
  void refresh();
  void copy();
  
  Key getKey();
  
  Component getComponent();
}
