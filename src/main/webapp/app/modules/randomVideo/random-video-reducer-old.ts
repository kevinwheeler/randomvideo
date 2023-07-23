import axios from 'axios';
import { createAsyncThunk, createSlice, } from '@reduxjs/toolkit';
import { createEntitySlice, EntityState, serializeAxiosError } from 'app/shared/reducers/reducer.utils';
import { IVideo, defaultValue } from 'app/shared/model/video.model';
import { IRootState } from 'app/config/store';
import qs from 'qs';

const initialState: RandomVideoState = {
  currentVideoIndex: -1,
  currentVideo: defaultValue,
  errorMessage: null,
  hasPreviousVideo: false,
  hasNextVideo: true,
  loading: false,
  videos: [],
  videoIdArray: [],
};

interface RandomVideoState {
  currentVideoIndex: number;
  currentVideo: IVideo | null;
  errorMessage: string | null;
  hasPreviousVideo: boolean;
  hasNextVideo: boolean;
  loading: boolean;
  videos: IVideo[];
  videoIdArray: string[];
}

export const nextVideo = createAsyncThunk(
  'randomVideo/next_random_video',
  async (slug: string, { getState }) => {
    const state = (getState() as IRootState).randomVideo;
    let newVideo = null;
    const isNextVideoAvailable = state.currentVideoIndex < state.videos.length - 1;
    if (!isNextVideoAvailable && state.hasNextVideo) {
      const requestUrl = `api/video-lists/${slug}/randomvideo`;
      const response = await axios.get<IVideo>(requestUrl, {
        params: {
          previouslySeenVideos: state.videoIdArray,
        },
        paramsSerializer: params => qs.stringify(params, {arrayFormat: 'repeat'}),
      });
      newVideo = response.data;
    }
    return { newlyFetchedVideo: newVideo };
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
        state.hasNextVideo = true;
      } else {
        state.hasPreviousVideo = false;
      }
    }
  },
  extraReducers(builder) {
    builder
      .addCase(nextVideo.fulfilled, (state, action) => {
        // May need to advance the video whether or not there is a new video in actiona.payload.video
        // because we may have clicked previousVideo and then nextVideo
        state.loading = false;
        if(action.payload.newlyFetchedVideo) {
          state.videos.push(action.payload.newlyFetchedVideo);
          const idSet = new Set(state.videoIdArray);
          idSet.add(action.payload.newlyFetchedVideo.id);
          state.videoIdArray = Array.from(idSet);
        } 
          state.hasNextVideo = state.currentVideoIndex < state.videos.length - 1;


          // state.currentVideoIndex = state.currentVideoIndex + 1;
          // state.currentVideo = state.videos[state.currentVideoIndex];
          // state.hasPreviousVideo = true;
      })
      .addCase(nextVideo.pending, state => {
        state.errorMessage = null;
        state.loading = true;
      })
      .addCase(nextVideo.rejected, (state, action) => {
        state.loading = false;
        state.errorMessage = action.error.message;
        // state.hasNextVideo = false;
      });
  },
});

export const { reset, previousVideo} = RandomVideoSlice.actions;

// Reducer
export default RandomVideoSlice.reducer;