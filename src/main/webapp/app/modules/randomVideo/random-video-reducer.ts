import axios from 'axios';
import { createAsyncThunk, } from '@reduxjs/toolkit';
import { createEntitySlice, EntityState, serializeAxiosError } from 'app/shared/reducers/reducer.utils';
import { IVideo, defaultValue } from 'app/shared/model/video.model';

const initialState: EntityState<IVideo> = {
  loading: false,
  errorMessage: null,
  entities: [],
  entity: defaultValue,
  updating: false,
  updateSuccess: false
};

// Actions
export const getRandomVideo = createAsyncThunk(
  'randomVideo/fetch_random_video',
  async (slug: string) => {
    console.log("in fetch random video");
    const requestUrl = `api/video-lists/${slug}/randomvideo`;
    // return axios.get<IVideo>(requestUrl);
    let reponse = axios.get<IVideo>(requestUrl);
    console.log("response: ", reponse);
    return reponse;
  },
  { serializeError: serializeAxiosError }
);

export const RandomVideoSlice = createEntitySlice({
  name: 'randomVideo',
  initialState,
  extraReducers(builder) {
    builder
      .addCase(getRandomVideo.fulfilled, (state, action) => {
        state.loading = false;
        state.entity = action.payload.data;
        console.log('action.payload.data: ', action.payload.data)
      })
      .addCase(getRandomVideo.pending, state => {
        state.errorMessage = null;
        state.loading = true;
        console.log("in pending");
      })
      .addCase(getRandomVideo.rejected, (state, action) => {
        state.loading = false;
        state.errorMessage = action.error.message;
        console.log("in rejected");
      });
  },
});

export const { reset } = RandomVideoSlice.actions;

// Reducer
export default RandomVideoSlice.reducer;
