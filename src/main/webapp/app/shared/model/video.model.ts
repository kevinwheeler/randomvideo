import { IXUser } from 'app/shared/model/x-user.model';
import { IVideoList } from 'app/shared/model/video-list.model';

export interface IVideo {
  id?: number;
  url?: string;
  name?: string;
  xUser?: IXUser | null;
  videoLists?: IVideoList[] | null;
}

export const defaultValue: Readonly<IVideo> = {};
