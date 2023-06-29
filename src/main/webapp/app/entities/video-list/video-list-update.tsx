import React, { useState, useEffect } from 'react';
import { Link, useNavigate, useParams } from 'react-router-dom';
import { Button, Row, Col, FormText } from 'reactstrap';
import { isNumber, Translate, translate, ValidatedField, ValidatedForm } from 'react-jhipster';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';

import { convertDateTimeFromServer, convertDateTimeToServer, displayDefaultDateTime } from 'app/shared/util/date-utils';
import { mapIdList } from 'app/shared/util/entity-utils';
import { useAppDispatch, useAppSelector } from 'app/config/store';

import { IVideo } from 'app/shared/model/video.model';
import { getEntities as getVideos } from 'app/entities/video/video.reducer';
import { IXUser } from 'app/shared/model/x-user.model';
import { getEntities as getXUsers } from 'app/entities/x-user/x-user.reducer';
import { IVideoList } from 'app/shared/model/video-list.model';
import { getEntity, updateEntity, createEntity, reset } from './video-list.reducer';

export const VideoListUpdate = () => {
  const dispatch = useAppDispatch();

  const navigate = useNavigate();

  const { id } = useParams<'id'>();
  const isNew = id === undefined;

  const videos = useAppSelector(state => state.video.entities);
  const xUsers = useAppSelector(state => state.xUser.entities);
  const videoListEntity = useAppSelector(state => state.videoList.entity);
  const loading = useAppSelector(state => state.videoList.loading);
  const updating = useAppSelector(state => state.videoList.updating);
  const updateSuccess = useAppSelector(state => state.videoList.updateSuccess);

  const handleClose = () => {
    navigate('/video-list');
  };

  useEffect(() => {
    if (isNew) {
      dispatch(reset());
    } else {
      dispatch(getEntity(id));
    }

    dispatch(getVideos({}));
    dispatch(getXUsers({}));
  }, []);

  useEffect(() => {
    if (updateSuccess) {
      handleClose();
    }
  }, [updateSuccess]);

  const saveEntity = values => {
    const entity = {
      ...videoListEntity,
      ...values,
      videos: mapIdList(values.videos),
      xUser: xUsers.find(it => it.id.toString() === values.xUser.toString()),
    };

    if (isNew) {
      dispatch(createEntity(entity));
    } else {
      dispatch(updateEntity(entity));
    }
  };

  const defaultValues = () =>
    isNew
      ? {}
      : {
          ...videoListEntity,
          videos: videoListEntity?.videos?.map(e => e.id.toString()),
          xUser: videoListEntity?.xUser?.id,
        };

  return (
    <div>
      <Row className="justify-content-center">
        <Col md="8">
          <h2 id="randomvideoApp.videoList.home.createOrEditLabel" data-cy="VideoListCreateUpdateHeading">
            <Translate contentKey="randomvideoApp.videoList.home.createOrEditLabel">Create or edit a VideoList</Translate>
          </h2>
        </Col>
      </Row>
      <Row className="justify-content-center">
        <Col md="8">
          {loading ? (
            <p>Loading...</p>
          ) : (
            <ValidatedForm defaultValues={defaultValues()} onSubmit={saveEntity}>
              {!isNew ? (
                <ValidatedField
                  name="id"
                  required
                  readOnly
                  id="video-list-id"
                  label={translate('global.field.id')}
                  validate={{ required: true }}
                />
              ) : null}
              <ValidatedField
                label={translate('randomvideoApp.videoList.videoListUrlSlug')}
                id="video-list-videoListUrlSlug"
                name="videoListUrlSlug"
                data-cy="videoListUrlSlug"
                type="text"
              />
              <ValidatedField
                label={translate('randomvideoApp.videoList.video')}
                id="video-list-video"
                data-cy="video"
                type="select"
                multiple
                name="videos"
              >
                <option value="" key="0" />
                {videos
                  ? videos.map(otherEntity => (
                      <option value={otherEntity.id} key={otherEntity.id}>
                        {otherEntity.id}
                      </option>
                    ))
                  : null}
              </ValidatedField>
              <ValidatedField
                id="video-list-xUser"
                name="xUser"
                data-cy="xUser"
                label={translate('randomvideoApp.videoList.xUser')}
                type="select"
              >
                <option value="" key="0" />
                {xUsers
                  ? xUsers.map(otherEntity => (
                      <option value={otherEntity.id} key={otherEntity.id}>
                        {otherEntity.id}
                      </option>
                    ))
                  : null}
              </ValidatedField>
              <Button tag={Link} id="cancel-save" data-cy="entityCreateCancelButton" to="/video-list" replace color="info">
                <FontAwesomeIcon icon="arrow-left" />
                &nbsp;
                <span className="d-none d-md-inline">
                  <Translate contentKey="entity.action.back">Back</Translate>
                </span>
              </Button>
              &nbsp;
              <Button color="primary" id="save-entity" data-cy="entityCreateSaveButton" type="submit" disabled={updating}>
                <FontAwesomeIcon icon="save" />
                &nbsp;
                <Translate contentKey="entity.action.save">Save</Translate>
              </Button>
            </ValidatedForm>
          )}
        </Col>
      </Row>
    </div>
  );
};

export default VideoListUpdate;
