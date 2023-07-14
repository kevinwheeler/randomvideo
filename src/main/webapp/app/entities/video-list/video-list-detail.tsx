import React, { useEffect } from 'react';
import { Link, useParams } from 'react-router-dom';
import { Button, Row, Col } from 'reactstrap';
import { Translate } from 'react-jhipster';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';

import { useAppDispatch, useAppSelector } from 'app/config/store';

import { getEntity } from './video-list.reducer';

export const VideoListDetail = () => {
  const dispatch = useAppDispatch();

  const { id } = useParams<'id'>();

  useEffect(() => {
    dispatch(getEntity(id));
  }, []);

  const videoListEntity = useAppSelector(state => state.videoList.entity);
  return (
    <Row>
      <Col md="8">
        <h2 data-cy="videoListDetailsHeading">
          <Translate contentKey="randomvideoApp.videoList.detail.title">VideoList</Translate>
        </h2>
        <dl className="jh-entity-details">
          <dt>
            <span id="id">
              <Translate contentKey="global.field.id">ID</Translate>
            </span>
          </dt>
          <dd>{videoListEntity.id}</dd>
          <dt>
            <span id="name">
              <Translate contentKey="randomvideoApp.videoList.name">Name</Translate>
            </span>
          </dt>
          <dd>{videoListEntity.name}</dd>
          <dt>
            <span id="slug">
              <Translate contentKey="randomvideoApp.videoList.slug">Slug</Translate>
            </span>
          </dt>
          <dd>{videoListEntity.slug}</dd>
          <dt>
            <Translate contentKey="randomvideoApp.videoList.video">Video</Translate>
          </dt>
          <dd>
            {videoListEntity.videos
              ? videoListEntity.videos.map((val, i) => (
                  <span key={val.id}>
                    <a>{val.id}</a>
                    {videoListEntity.videos && i === videoListEntity.videos.length - 1 ? '' : ', '}
                  </span>
                ))
              : null}
          </dd>
          <dt>
            <Translate contentKey="randomvideoApp.videoList.xUser">X User</Translate>
          </dt>
          <dd>{videoListEntity.xUser ? videoListEntity.xUser.id : ''}</dd>
        </dl>
        <Button tag={Link} to="/video-list" replace color="info" data-cy="entityDetailsBackButton">
          <FontAwesomeIcon icon="arrow-left" />{' '}
          <span className="d-none d-md-inline">
            <Translate contentKey="entity.action.back">Back</Translate>
          </span>
        </Button>
        &nbsp;
        <Button tag={Link} to={`/video-list/${videoListEntity.id}/edit`} replace color="primary">
          <FontAwesomeIcon icon="pencil-alt" />{' '}
          <span className="d-none d-md-inline">
            <Translate contentKey="entity.action.edit">Edit</Translate>
          </span>
        </Button>
      </Col>
    </Row>
  );
};

export default VideoListDetail;
