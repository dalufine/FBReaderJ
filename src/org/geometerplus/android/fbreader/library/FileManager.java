/*
 * Copyright (C) 2010-2011 Geometer Plus <contact@geometerplus.com>
 *
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA
 * 02110-1301, USA.
 */

package org.geometerplus.android.fbreader.library;

import java.util.ArrayList;

import android.content.Intent;
import android.os.Bundle;

import org.geometerplus.fbreader.library.FileTree;
import org.geometerplus.fbreader.library.Book;
import org.geometerplus.fbreader.tree.FBTree;

public final class FileManager extends BaseActivity {
	@Override
	public void onCreate(Bundle icicle) {
		super.onCreate(icicle);

		final ListAdapter adapter = new ListAdapter(this, new ArrayList<FBTree>());
		adapter.addAll(myCurrentTree.subTrees());
		setSelection(getListAdapter().getFirstSelectedItemIndex());

		getListView().setTextFilterEnabled(true);
	}

	private void startUpdate() {
		new Thread(new Runnable() {
			public void run() {
				myCurrentTree.waitForOpening();
				getListAdapter().clear();
				getListAdapter().addAll(myCurrentTree.subTrees());
			}
		}).start();
	}

	@Override
	protected void onActivityResult(int requestCode, int returnCode, Intent intent) {
		if (requestCode == CHILD_LIST_REQUEST && returnCode == RESULT_DO_INVALIDATE_VIEWS) {
			if (myCurrentTree instanceof FileTree) {
				startUpdate();
			}
			getListView().invalidateViews();
			setResult(RESULT_DO_INVALIDATE_VIEWS);
		} else if (requestCode == BOOK_INFO_REQUEST) {
			getListView().invalidateViews();
		}
	} 

	@Override
	protected void deleteBook(Book book, int mode) {
		super.deleteBook(book, mode);
		getListAdapter().remove(new FileTree((FileTree)myCurrentTree, book.File));
		getListView().invalidateViews();
	}
}
