import axios from 'axios';
import { createAsyncThunk, createSlice } from '@reduxjs/toolkit';
import { serializeAxiosError } from 'app/shared/reducers/reducer.utils';
import { IVideo, defaultValue } from 'app/shared/model/video.model';
import { IRootState } from 'app/config/store';
import _ from 'lodash'; // for shuffling

const initialState: RandomVideoState = {
  currentVideoIndex: -1,
  currentVideo: defaultValue,
  errorMessage: null,
  hasPreviousVideo: false,
  hasNextVideo: false,
  loading: false,
  videos: [],
};

interface RandomVideoState {
  currentVideoIndex: number;
  currentVideo: IVideo | null;
  errorMessage: string | null;
  hasPreviousVideo: boolean;
  hasNextVideo: boolean;
  loading: boolean;
  videos: IVideo[];
}

export const fetchVideos = createAsyncThunk(
  'randomVideo/fetch_videos',
  async (slug: string) => {
    const requestUrl = `/api/video-lists/by-slug/${slug}`;
    const response = await axios.get<Set<IVideo>>(requestUrl);
    const videos = _.shuffle(Array.from(response.data));
    return videos;
  },
  { serializeError: serializeAxiosError }
);

export const RandomVideoSlice = createSlice({
  name: 'randomVideo',
  initialState,
  reducers: {
    previousVideo(state) {
      if (state.currentVideoIndex > 0) {
        state.currentVideoIndex -= 1;
        state.currentVideo = state.videos[state.currentVideoIndex];
        state.hasPreviousVideo = state.currentVideoIndex > 0;
        state.hasNextVideo = true;
      }
    },
    nextVideo(state) {
      if (state.currentVideoIndex < state.videos.length - 1) {
        state.currentVideoIndex += 1;
        state.currentVideo = state.videos[state.currentVideoIndex];
        state.hasNextVideo = state.currentVideoIndex < state.videos.length - 1;
        state.hasPreviousVideo = true;
      }
    },
  },
  extraReducers: (builder) => {
    builder
      .addCase(fetchVideos.pending, (state) => {
        state.errorMessage = null;
        state.loading = true;
      })
      .addCase(fetchVideos.fulfilled, (state, action) => {
        state.loading = false;
        state.videos = action.payload;
        state.currentVideoIndex = 0;
        state.currentVideo = state.videos[0];
        state.hasNextVideo = state.videos.length > 0;
        state.hasPreviousVideo = false;
      })
      .addCase(fetchVideos.rejected, (state, action) => {
        state.loading = false;
        state.errorMessage = action.error.message;
      });
  },
});

export const { previousVideo, nextVideo } = RandomVideoSlice.actions;

export default RandomVideoSlice.reducer;