import React from 'react';
import { Route } from 'react-router-dom';

import ErrorBoundaryRoutes from 'app/shared/error/error-boundary-routes';

import Video from './video';
import VideoDetail from './video-detail';
import VideoUpdate from './video-update';
import VideoDeleteDialog from './video-delete-dialog';

const VideoRoutes = () => (
  <ErrorBoundaryRoutes>
    <Route index element={<Video />} />
    <Route path="new" element={<VideoUpdate />} />
    <Route path=":id">
      <Route index element={<VideoDetail />} />
      <Route path="edit" element={<VideoUpdate />} />
      <Route path="delete" element={<VideoDeleteDialog />} />
    </Route>
  </ErrorBoundaryRoutes>
);

export default VideoRoutes;
