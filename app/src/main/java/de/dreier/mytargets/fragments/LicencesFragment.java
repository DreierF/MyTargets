/*
 * Copyright (C) 2016 Kane O'Riley
 *
 * Licensed under the Apache License, Version 2.0 (the "License")
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package de.dreier.mytargets.fragments;

import android.os.Bundle;
import android.os.Parcelable;
import android.support.annotation.CallSuper;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import de.dreier.mytargets.R;
import de.dreier.mytargets.utils.DividerItemDecoration;
import me.oriley.homage.Homage;
import me.oriley.homage.recyclerview.HomageAdapter;
import me.oriley.homage.recyclerview.HomageView;

@SuppressWarnings("WeakerAccess")
public final class LicencesFragment extends Fragment {

    private static final String KEY_LAYOUT_MANAGER_STATE = "layoutManagerState";
    @NonNull
    private RecyclerView mRecyclerView;
    @Nullable
    private RecyclerView.Adapter mAdapter;
    @Nullable
    private RecyclerView.LayoutManager mLayoutManager;

    @NonNull
    public RecyclerView.Adapter createAdapter() {
        Homage homage = new Homage(getActivity(), R.raw.licences);

        // Adds a custom license definition to enable matching in your JSON list
        homage.addLicense("epl", R.string.license_epl_name, R.string.license_epl_url,
                R.string.license_epl_description);

        homage.refreshLibraries();

        mRecyclerView.addItemDecoration(
                new DividerItemDecoration(getContext(), R.drawable.full_divider));
        return new HomageAdapter(homage, HomageView.ExtraInfoMode.EXPANDABLE, false);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_list, container, false);
    }

    @CallSuper
    @Override
    public void onViewCreated(View view, @Nullable final Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        RecyclerView recyclerView = (RecyclerView) view.findViewById(android.R.id.list);
        if (recyclerView == null) {
            throw new IllegalStateException("Required views not found");
        }

        mRecyclerView = recyclerView;
        mRecyclerView.setHasFixedSize(true);

        mLayoutManager = new LinearLayoutManager(getActivity());
        mRecyclerView.setLayoutManager(mLayoutManager);
        if (savedInstanceState != null) {
            Parcelable layoutState = savedInstanceState.getParcelable(KEY_LAYOUT_MANAGER_STATE);
            if (mLayoutManager != null) {
                mLayoutManager.onRestoreInstanceState(layoutState);
            }
        }

        mAdapter = createAdapter();
        mRecyclerView.setAdapter(mAdapter);
    }

    @Nullable
    public RecyclerView.Adapter getAdapter() {
        return mAdapter;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        if (mLayoutManager != null) {
            outState.putParcelable(KEY_LAYOUT_MANAGER_STATE, mLayoutManager.onSaveInstanceState());
        }
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onDestroyView() {
        mRecyclerView.setAdapter(null);
        mRecyclerView.setLayoutManager(null);
        super.onDestroyView();
    }
}
