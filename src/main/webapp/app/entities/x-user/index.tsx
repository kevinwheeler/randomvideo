import React from 'react';
import { Route } from 'react-router-dom';

import ErrorBoundaryRoutes from 'app/shared/error/error-boundary-routes';

import XUser from './x-user';
import XUserDetail from './x-user-detail';
import XUserUpdate from './x-user-update';
import XUserDeleteDialog from './x-user-delete-dialog';

const XUserRoutes = () => (
  <ErrorBoundaryRoutes>
    <Route index element={<XUser />} />
    <Route path="new" element={<XUserUpdate />} />
    <Route path=":id">
      <Route index element={<XUserDetail />} />
      <Route path="edit" element={<XUserUpdate />} />
      <Route path="delete" element={<XUserDeleteDialog />} />
    </Route>
  </ErrorBoundaryRoutes>
);

export default XUserRoutes;
