import React from 'react';
import { Route } from 'react-router-dom';

import ErrorBoundaryRoutes from 'app/shared/error/error-boundary-routes';

import VideoList from './video-list';
import VideoListDetail from './video-list-detail';
import VideoListUpdate from './video-list-update';
import VideoListDeleteDialog from './video-list-delete-dialog';

const VideoListRoutes = () => (
  <ErrorBoundaryRoutes>
    <Route index element={<VideoList />} />
    <Route path="new" element={<VideoListUpdate />} />
    <Route path=":id">
      <Route index element={<VideoListDetail />} />
      <Route path="edit" element={<VideoListUpdate />} />
      <Route path="delete" element={<VideoListDeleteDialog />} />
    </Route>
  </ErrorBoundaryRoutes>
);

export default VideoListRoutes;
