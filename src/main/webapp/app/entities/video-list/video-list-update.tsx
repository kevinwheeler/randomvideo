import React, { useState, useEffect } from 'react';
import { Link, useNavigate, useParams } from 'react-router-dom';
import { Button, Row, Col } from 'reactstrap';
import { isNumber, Translate, translate, ValidatedField, ValidatedForm } from 'react-jhipster';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';
import Select from 'react-select'; // Import the react-select component

import { convertDateTimeFromServer, convertDateTimeToServer, displayDefaultDateTime } from 'app/shared/util/date-utils';
import { mapIdList } from 'app/shared/util/entity-utils';
import { useAppDispatch, useAppSelector } from 'app/config/store';

import { IVideo } from 'app/shared/model/video.model';
import { getEntities as getVideos } from 'app/entities/video/video.reducer';
import { IVideoList } from 'app/shared/model/video-list.model';
import { getEntity, updateEntity, createEntity, reset } from './video-list.reducer';

export const VideoListUpdate = () => {
  type OptionType = {
    label: string;
    value: string;
  };

  const dispatch = useAppDispatch();

  const navigate = useNavigate();

  const { id } = useParams<'id'>();
  const isNew = id === undefined;

  const videos = useAppSelector(state => state.video.entities);
  const videoListEntity = useAppSelector(state => state.videoList.entity);
  const loading = useAppSelector(state => state.videoList.loading);
  const updating = useAppSelector(state => state.videoList.updating);
  const updateSuccess = useAppSelector(state => state.videoList.updateSuccess);
  const [selectedVideoOptions, setSelectedVideoOptions] = useState<OptionType[]>(null);

  useEffect(() => {
    if (videoListEntity && videoListEntity.videos && !selectedVideoOptions) {
      let selectedOptions:OptionType[] = videoListEntity.videos.map(video => ({ label: video.name, value: video.id }));
      setSelectedVideoOptions(selectedOptions);
    }
  }, [videoListEntity]);
  

  const handleClose = () => {
    navigate('/video-list');
  };

  useEffect(() => {
    if (isNew) {
      dispatch(reset());
    } else {
      dispatch(getEntity(id));
    }

    dispatch(getVideos({query: "user=current"}));
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
      videos: selectedVideoOptions ? selectedVideoOptions.map(option => ({ id: option.value })) : [],
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
                label={translate('randomvideoApp.videoList.name')}
                id="video-list-name"
                name="name"
                data-cy="name"
                type="text"
                validate={{
                  required: { value: true, message: translate('entity.validation.required') },
                  minLength: { value: 1, message: translate('entity.validation.minlength', { min: 1 }) },
                  maxLength: { value: 50, message: translate('entity.validation.maxlength', { max: 50 }) },
                }}
              />
              <ValidatedField
                label={translate('randomvideoApp.videoList.slug') + ". " + translate('randomvideoApp.videoList.slugAttributeDescription')}
                id="video-list-slug"
                name="slug"
                data-cy="slug"
                type="text"
                validate={{
                  required: { value: true, message: translate('entity.validation.required') },
                  minLength: { value: 1, message: translate('entity.validation.minlength', { min: 1 }) },
                  maxLength: { value: 50, message: translate('entity.validation.maxlength', { max: 50 }) },
                  pattern: {
                    value: /^(?!(api|internal-use)$)[a-zA-Z0-9-]+$/,
                    message: translate('entity.validation.pattern', { pattern: '^(?!(api|internal-use)$)[a-zA-Z0-9-]+$' }),
                  },
                }}
              />
              <label>
                {translate('randomvideoApp.videoList.video') + ". " + translate('randomvideoApp.videoList.videoAttributeDescription')}
              </label>

              <Select
                isMulti
                name="videos"
                value={selectedVideoOptions}
                onChange={(selectedOptions: OptionType[], actionMeta) => {
                   setSelectedVideoOptions(selectedOptions);
                  }
                }
                options={videos.map(video => ({ value: video.id, label: video.name }))}
                className="basic-multi-select"
                classNamePrefix="select"
                styles={{
                  option: (provided, state) => ({
                    ...provided,
                    color: state.isDisabled ? '#ccc' : '#333',
                    backgroundColor: state.isSelected ? '#0052cc' : state.isFocused ? '#0052cc33' : null,
                  }),
                }}
              />
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