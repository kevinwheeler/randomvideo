import { IUser } from 'app/shared/model/user.model';
import { IVideoList } from 'app/shared/model/video-list.model';
import { IVideo } from 'app/shared/model/video.model';

export interface IXUser {
  id?: number;
  internalUser?: IUser | null;
  videoLists?: IVideoList[] | null;
  videos?: IVideo[] | null;
}

export const defaultValue: Readonly<IXUser> = {};
