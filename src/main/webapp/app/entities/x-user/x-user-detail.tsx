import React, { useEffect } from 'react';
import { Link, useParams } from 'react-router-dom';
import { Button, Row, Col } from 'reactstrap';
import { Translate } from 'react-jhipster';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';

import { useAppDispatch, useAppSelector } from 'app/config/store';

import { getEntity } from './x-user.reducer';

export const XUserDetail = () => {
  const dispatch = useAppDispatch();

  const { id } = useParams<'id'>();

  useEffect(() => {
    dispatch(getEntity(id));
  }, []);

  const xUserEntity = useAppSelector(state => state.xUser.entity);
  return (
    <Row>
      <Col md="8">
        <h2 data-cy="xUserDetailsHeading">
          <Translate contentKey="randomvideoApp.xUser.detail.title">XUser</Translate>
        </h2>
        <dl className="jh-entity-details">
          <dt>
            <span id="id">
              <Translate contentKey="global.field.id">ID</Translate>
            </span>
          </dt>
          <dd>{xUserEntity.id}</dd>
          <dt>
            <span id="videoListUrlSlug">
              <Translate contentKey="randomvideoApp.xUser.videoListUrlSlug">Video List Url Slug</Translate>
            </span>
          </dt>
          <dd>{xUserEntity.videoListUrlSlug}</dd>
          <dt>
            <Translate contentKey="randomvideoApp.xUser.internalUser">Internal User</Translate>
          </dt>
          <dd>{xUserEntity.internalUser ? xUserEntity.internalUser.id : ''}</dd>
        </dl>
        <Button tag={Link} to="/x-user" replace color="info" data-cy="entityDetailsBackButton">
          <FontAwesomeIcon icon="arrow-left" />{' '}
          <span className="d-none d-md-inline">
            <Translate contentKey="entity.action.back">Back</Translate>
          </span>
        </Button>
        &nbsp;
        <Button tag={Link} to={`/x-user/${xUserEntity.id}/edit`} replace color="primary">
          <FontAwesomeIcon icon="pencil-alt" />{' '}
          <span className="d-none d-md-inline">
            <Translate contentKey="entity.action.edit">Edit</Translate>
          </span>
        </Button>
      </Col>
    </Row>
  );
};

export default XUserDetail;
