import { IVideoList } from 'app/shared/model/video-list.model';

export interface IVideo {
  id?: number;
  url?: string | null;
  videoLists?: IVideoList[] | null;
}

export const defaultValue: Readonly<IVideo> = {};
