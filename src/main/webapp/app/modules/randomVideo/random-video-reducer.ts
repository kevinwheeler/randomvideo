import axios from 'axios';
import { createAsyncThunk, createSlice, } from '@reduxjs/toolkit';
import { createEntitySlice, EntityState, serializeAxiosError } from 'app/shared/reducers/reducer.utils';
import { IVideo, defaultValue } from 'app/shared/model/video.model';
import { IRootState } from 'app/config/store';

const initialState: RandomVideoState = {
  currentVideoIndex: -1,
  currentVideo: defaultValue,
  errorMessage: null,
  hasPreviousVideo: false,
  loading: false,
  videos: [],
};

interface RandomVideoState {
  currentVideoIndex: number;
  currentVideo: IVideo | null;
  errorMessage: string | null;
  hasPreviousVideo: boolean;
  loading: boolean;
  videos: IVideo[];
}

export const nextVideo = createAsyncThunk(
  'randomVideo/next_random_video',
  async (slug: string, { getState }) => {
    const state = (getState() as IRootState).randomVideo;
    let newVideo = null;
    const isNextVideoAvailable = state.currentVideoIndex < state.videos.length - 1
    if (!isNextVideoAvailable) {
      const requestUrl = `api/video-lists/${slug}/randomvideo`;
      const response = await axios.get<IVideo>(requestUrl);
      newVideo = response.data;
    }
    return { video: newVideo };
  },
  { serializeError: serializeAxiosError }
);

export const RandomVideoSlice = createSlice({
  name: 'randomVideo',
  initialState,
  reducers: {
    reset() {
      return initialState;
    },
    previousVideo(state) {
      if (state.currentVideoIndex > 0) {
        state.currentVideoIndex -= 1;
        state.currentVideo = state.videos[state.currentVideoIndex];
        state.hasPreviousVideo = state.currentVideoIndex > 0;
      } else {
        state.hasPreviousVideo = false;
      }
    }
  },
  extraReducers(builder) {
    builder
      .addCase(nextVideo.fulfilled, (state, action) => {
        state.loading = false;
        if(action.payload.video) {
          state.videos.push(action.payload.video);
        }
        state.currentVideoIndex = state.currentVideoIndex + 1;
        state.currentVideo = state.videos[state.currentVideoIndex];
        state.hasPreviousVideo = state.currentVideoIndex > 0;
      })
      .addCase(nextVideo.pending, state => {
        state.errorMessage = null;
        state.loading = true;
      })
      .addCase(nextVideo.rejected, (state, action) => {
        state.loading = false;
        state.errorMessage = action.error.message;
      });
  },
});

export const { reset, previousVideo} = RandomVideoSlice.actions;

// Reducer
export default RandomVideoSlice.reducer;