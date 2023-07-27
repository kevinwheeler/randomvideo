import React, { useEffect } from 'react';
import { Button, Col, Row } from 'reactstrap';
import { locales, languages } from 'app/config/translation';
import { useAppDispatch, useAppSelector } from 'app/config/store';
import { fetchVideos, nextVideo, previousVideo } from './random-video-reducer';
import { useParams, useLocation } from 'react-router-dom';
import './random-video.scss';
import { Translate, translate } from 'react-jhipster';

import { sanitizeUrl } from "@braintree/sanitize-url";

export const RandomVideoPage = () => {
  const dispatch = useAppDispatch();
  const randomVideo = useAppSelector(state => state.randomVideo.currentVideo);
  const hasPreviousVideo = useAppSelector(state => state.randomVideo.hasPreviousVideo);
  const hasNextVideo = useAppSelector(state => state.randomVideo.hasNextVideo);
  let { slug } = useParams();
  let location = useLocation();

  // if we're on the homepage, use a hardcoded slug
  if (location.pathname === "/") {
    slug = "homepage";
  }


  useEffect(() => {
    dispatch(fetchVideos(slug));
  }, [dispatch, slug]);


  // Add autoplay and mute to query params if not present
  // Remove videoIdParam if passed as argument
  const prepareQueryParams = (parsedUrl: URL, videoIdParam?: string) => {
    const queryParams = parsedUrl.searchParams;
    
    if(videoIdParam) {
      queryParams.delete(videoIdParam);
    }
  
    if(!queryParams.has('autoplay')) {
      queryParams.set('autoplay', '1');
    }

    if(queryParams.get('autoplay') === '1' && !queryParams.has('mute')) {
      queryParams.set('mute', '1');
    }
  
    return queryParams.toString();
  }
  
  const getVideoIdAndQueryParams = (url: string) => {
    if (!url) {
      return null;
    }
    const sanitizedUrl = sanitizeUrl(url);
  
    // Prepend 'https://' if not present
    const urlWithProtocol = sanitizedUrl.startsWith('https://') 
      ? sanitizedUrl 
      : 'https://' + sanitizedUrl;
    
    const parsedUrl = new URL(urlWithProtocol);
  
    if(parsedUrl.hostname.endsWith('youtu.be')) {
      let videoId = parsedUrl.pathname.split('/')[1];
      const queryParamsString = prepareQueryParams(parsedUrl);
      return `${videoId}?${queryParamsString}`;
    } else if(parsedUrl.hostname.endsWith('youtube.com')) {
      const videoId = parsedUrl.searchParams.get('v');
      const queryParamsString = prepareQueryParams(parsedUrl, 'v');
      return `${videoId}?${queryParamsString}`;
    } else {
      return null;
    }
  }

  
  const sanitizeString = (string: string): string => {
    const entityMap = {
      '&': '&amp;',
      '<': '&lt;',
      '>': '&gt;',
      '"': '&quot;',
      "'": '&#39;',
      '/': '&#x2F;',
      '`': '&#x60;',
      '=': '&#x3D;'
    };

    return String(string).replace(/[&<>"'`=\/]/g, function (s) {
      return entityMap[s];
    });
  }

  function dispatchPreviousVideo(): void {
    dispatch(previousVideo());
  }

  function dispatchNextVideo(): void {
    dispatch(nextVideo());
  }

  return (
    <div className="video-page">
      <Row className="justify-content-center">
        <Col md="8">
          <h1 className="video-title">{sanitizeString(randomVideo.name)}</h1>
          <div className="video-container video-responsive">
            <iframe
              width="853"
              height="480"
              src={`https://www.youtube.com/embed/${getVideoIdAndQueryParams(randomVideo.url)}`}
              allow="accelerometer; autoplay; clipboard-write; encrypted-media; gyroscope; picture-in-picture"
              allowFullScreen
              title="Embedded youtube"
            />
          </div>
          <div className="video-controls">
            <div className="button-group">
              <Button onClick={dispatchPreviousVideo} disabled={!hasPreviousVideo}>{translate("randomVideo.previousVideo")}</Button>
              <Button onClick={dispatchNextVideo} disabled={!hasNextVideo}>{ hasNextVideo ? translate("randomVideo.nextVideo") : translate("randomVideo.noVideosLeft")}</Button>
            </div>
          </div>

          {/* A call to action letting people know they can create their own video lists */}
          <div className="cta-container text-center">
            <p>
               <Translate contentKey="randomVideo.createVideoListDescription">Create your own video list here!</Translate>
            </p>
            <p>
              <a className="btn btn-create-list" href="/video-list/new">
                {translate("randomVideo.createVideoListButton")}
              </a>
            </p>
          </div>
        </Col>
      </Row>
    </div>
  );
};

export default RandomVideoPage;
